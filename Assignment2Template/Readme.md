# GRPC Setup (if you encounter errors)

1. Please make sure that you did not change anything in the template.
2. Inside the project settings, set the JDK to a JDK that is at least version 17.
3. Run the maven goal "protobuf:compile", then "protobuf:compile-custom" and then reload the maven project.
4. You should be able to run the application now.

# Web Debugging Steps

1. It is often easier to first use tools like Postman or the Intellij inbuilt HTTP Editor (just create an `.http` file) to manually create your API calls.
2. Then transform these calls into Java code and work with that.
3. Many APIs also offer a Swagger Interface, which makes it easier to understand how they work.
