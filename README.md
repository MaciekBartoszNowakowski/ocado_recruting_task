Maciek_Nowakowski_Java_Krakow

I used Java 17 and Gradle to write this code.
As a solution, I created a dynamic algorithm with a time complexity of O(n^3).

This code works as follows: I create a three-dimensional array in which the rows represent each subsequent product in the
basket. The columns describe each possible supplier and the third dimension stores information about the suppliers used so far.
For each possible product, I check the information on the minimum number of suppliers needed to deliver the previous products,
taking into account the shipment of this product by each currently available supplier.
For the last one I look for the minimum of the number of suppliers in the row assigned to it. Those suppliers are optimal to choose.
At the very end, I run a function that greedily assigns as many items as possible to one of the suppliers.

