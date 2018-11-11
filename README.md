# CloudFormation Custom Resource Provisioning Lambda

This library allow you simple write custom provisioning logic in CloudFormation templates.

[![Build Status](https://travis-ci.org/vitalibo/cfn-custom-resource.svg?branch=master)](https://travis-ci.org/vitalibo/cfn-custom-resource)

## Usage

This guide prodvide a sampling of how [cfn-custom-resource](https://github.com/vitalibo/cfn-custom-resource) library helps you accelerate and facilitate CloudFormation Custom Resources development.
You'll implement simple CloudFormation Custom Resource for orchestration Apache Spark applications. Complete implementation you can find in this repository [spark-aws-orchestration](https://github.com/vitalibo/spark-aws-orchestration/tree/master/spark-cfn-orchestration).

#### Build with Maven

First you need create a maven project, and include following dependency in it:

```xml
<dependency>
    <groupId>com.github.vitalibo</groupId>
    <artifactId>cfn-custom-resource</artifactId>
    <version>1.0.0</version>
</dependency>
```

This library located on S3 remote repository, given this, add it repository in your `pom.xml`:

```xml
<build>
    <extensions>
        <extension>
            <groupId>org.kuali.maven.wagons</groupId>
            <artifactId>maven-s3-wagon</artifactId>
            <version>1.2.1</version>
        </extension>
    </extensions>
</build>
 
<repositories>
    <repository>
        <id>repo.vitalibo.github.com</id>
        <url>s3://public.repo.maven.vitalibo.github.com/release</url>
    </repository>
</repositories>
```

#### Create a Custom Resource provisioning lambda

Now you can create a custom `ResourceProperties` and `ResourceData` for Custom Resource provision lambda.

```java
import com.github.vitalibo.cfn.resource.model.ResourceProperties;

@Data
public class SparkJobResourceProperties extends ResourceProperties {
    
    @JsonProperty(value = "ClassName")
    private String className;
    ...
    
}
```

```java
import com.github.vitalibo.cfn.resource.model.ResourceData;

@Data
public class SparkJobResourceData extends ResourceData<SparkJobResourceData> {

    @JsonProperty(value = "ApplicationId")
    private String applicationId;
    ...
    
}
```

Next step is creating set of custom resource types binded with resource properties.
For this you need create enum and implement `ResourceType` interface.

```java
import com.github.vitalibo.cfn.resource.model.ResourceType;

@Getter
@RequiredArgsConstructor
public enum CustomResourceType implements ResourceType {
    
    ...
    SparkJob("Custom::SparkJob", SparkJobResourceProperties.class);
    
    private final String typeName;
    private final Class<? extends ResourceProperties> typeClass;
    
}
```

After describing types, you can move to implementation business behavior of custom resource types.
For this you need implement appropriate interfaces `CreateFacade`, `DeleteFacade` and `UpdateFacade`.
Also here you can add validation `Rules` and delegate it execution to facades.
Your custom rules must implement functional interface `Rules.Rule`.

```java
import com.github.vitalibo.cfn.resource.facade.CreateFacade;
import com.github.vitalibo.cfn.resource.util.Rules;

public class SparkJobCreateFacade implements CreateFacade<SparkJobResourceProperties, SparkJobResourceData> {

    @Delegate
    private final Rules<SparkJobResourceProperties> rules = new Rules<>(
        ValidationRules::verifyRule,
        ...
        ValidationRules::verifyRuleN);

    @Override
    public SparkJobResourceData create(SparkJobResourceProperties properties, Context context) throws ResourceProvisionException {
        ...
        return new SparkJobResourceData()
            .withPhysicalResourceId(physicalResourceId)
            .withApplicationId(applicationId);
    }

}
```

Now we have to gather everything in one, so, create class which extend `AbstractFactory` and implements methods `createCreateFacade`, `createDeleteFacade` and `createUpdateFacade` in which depends on `resourceType` you need create appropriate facade.

```java
import com.github.vitalibo.cfn.resource.AbstractFactory;
import com.github.vitalibo.cfn.resource.Facade;

public class Factory extends AbstractFactory<CustomResourceType> {

    Factory() {
        super(CustomResourceType.class);
    }

    @Override
    public Facade createCreateFacade(ResourceType resourceType) {
        switch (resourceType) {
            case SparkJob:
                return new SparkJobCreateFacade();
            default:
                throw new IllegalStateException();
        }
    }
    ...
    
}
```

Finally, you need create class which extended `ResourceProvisionHandler` class.
This class you should use as entry point for your custom provisioning lambda.

```java
import com.github.vitalibo.cfn.resource.ResourceProvisionHandler;

public class CustomResourceProvisionHandler extends ResourceProvisionHandler<CustomResourceType> {

    public CustomResourceProvisionHandler() {
        super(new Factory());
    }

}
```

#### Deployment your custom provision Lambda via CloudFormation stack

One of the main method deployment your provision lambda on AWS is CloudFormation stack. So, next template you can use as start point for deployment your applications.

```yaml
AWSTemplateFormatVersion: '2010-09-09'
Parameters:
  Name:
    Type: 'String'
    Description: 'Service name'
  Bucket:
    Type: 'String'
    Description: 'S3 bucket name where located source codes'
  SourceCode:
    Type: 'String'
    Description: 'S3 key where located source codes'
  ClassName:
    Type: 'String'
    Description: 'Class name of your provision lambda'
Resources:
  Lambda:
    Type: 'AWS::Lambda::Function'
    Properties:
      Code:
        S3Bucket: !Ref Bucket
        S3Key: !Ref SourceCode
      FunctionName: !Ref Name
      Handler: !Ref ClassName
      MemorySize: 512
      Role:
        Fn::GetAtt:
        - Role
        - Arn
      Runtime: 'java8'
      Timeout: 30
  Role:
    Type: 'AWS::IAM::Role'
    Properties:
      AssumeRolePolicyDocument:
        Version: 2012-10-17
        Statement:
        - Effect: Allow
          Principal:
            Service:
            - lambda.amazonaws.com
          Action:
          - 'sts:AssumeRole'
      Policies:
      - PolicyName: 'Runtime'
        PolicyDocument:
          Version: 2012-10-17
          Statement:
          - Effect: Allow
            Action:
            - 'logs:CreateLogStream'
            - 'logs:PutLogEvents'
            Resource: !GetAtt
            - LogGroup
            - Arn
      RoleName: !Ref Name
  LogGroup:
    Type: 'AWS::Logs::LogGroup'
    Properties:
      LogGroupName: !Sub '/aws/lambda/${Name}'
      RetentionInDays: 3
  Permission:
    Type: 'AWS::Lambda::Permission'
    Properties:
      Action: 'lambda:InvokeFunction'
      FunctionName: !GetAtt
      - Lambda
      - Arn
      Principal: cloudformation.amazonaws.com
      SourceAccount: !Ref 'AWS::AccountId'
      SourceArn: !Sub 'arn:aws:cloudformation:${AWS::Region}:${AWS::AccountId}:stack/*'
Outputs:
  LambdaArn:
    Value: !GetAtt
    - Lambda
    - Arn
    Export:
      Name: !Ref Name
```

Following sample demonstrate usage Apache Spark orchestration via CloudFormation.

```yaml
SparkPi:
  Type: 'Custom::SparkJob'
  Properties:
    ServiceToken: !Fn::ImportValue SparkProvisionLambdaArn
    Name: 'Spark Pi'
    ClassName: 'org.apache.spark.examples.SparkPi'
    NumExecutors: 1
    DriverMemory: '512m'
    ExecutorMemory: '512m'
    ExecutorCores: 1
    Args:
     - 10
```