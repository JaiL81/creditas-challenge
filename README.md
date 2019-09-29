# creditas-challenge
Repository for this challenge https://github.com/JaiL81/challenge/blob/master/backend/README_English.md


## Explanation of the solution
For this challenge, it was requested that some submission rules were applied depending on the type of product of the order. So to avoid the implementation of this logic in switch/if-else statements, I have removed the type property of products and I have refactored this so now we have `Product` as an abstract class with the abstract function `applySubmissionRules()`, wich is called inside `Order.finish()` function that is responsible to finish the order and apply the submission rules for each of the products.

Then I have created 4 different classes that extend from `Product` and that were taken from the different options that were available in `ProductType` enum:
* `PhysicalItem`
* `Subscription`
* `Book`
* `DigitalMedia`

Then in each of these classes I have added the specific implementation of the specified submission rules depending on the type of the product. In this way I think it will be easier in the future modifying or extending the functionality adding new type of products, as it would be enough to add a new class extending `Product` and adding the logic in the `applySubmissionRules()` function. I have grouped all these classes in the file `Product.kt`as I think it makes more sense.

In order to accomplish the requirements of the challenge, I have also created a `DiscountVoucher` class that is associated to the customer when adquiring a `Subscription` product.

## Tests
To ensure every change I have added to the codebase is working, I have created a test file called OrderProcessoTest.kt where I have placed 2 nested JUnit 5 inner classes, one to test the already existing funciontality (and to ensure that my changes don't break anything) of the payment stream called `PaymentTests` and another one called `SubmissionRulesTest` to test the applying of the different rules depending on the type of product.

Regarding the testing technologies, I have used JUnit5 as it was a fast option and covered almost everything I needed. In a real world scenario project I would have probably used Spek or Kotlintest, but JUnit5 was a faster and a relieble solution for me.

In order to add some mocking of the methods to send emails and print shipment lables, I have used MockK, which I have added as an external library to my IDE, so if you want to run the unit tests you need to add MockK 1.9.1 to your project. In a real project I would have created a maven project and add this as a dependency with the maven surefire plugin.

![Test execution](https://i.ibb.co/JCTCPh3/Captura-de-pantalla-2019-09-29-a-las-21-41-12.png)

If you need more details about the solution or have any question, don't hesitate in contacting me at jail81@gmail.com.

