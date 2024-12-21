The E-commerce Application is a modular, scalable, and distributed system designed using microservices architecture to address the challenges of modern e-commerce platforms. 
By breaking the application into independent, loosely coupled services, this system ensures flexibility, maintainability, and seamless scalability. 
Each microservice is responsible for a specific business function, such as User Management, Authentication, Product Catalog, Order Processing, Payments, Notifications, and more.

Following are different services present in this project
1.	User Service
  o	User registration and login.
  o	Manage user profiles (update, delete, view).
  o	Address management for shipping and billing.
2.	Authentication Service
  o	Token-based authentication using JWT.
  o	Support for role-based access control (RBAC).
  o	Access to endpoints using filter authorization.
3.	Product Service
  o	Catalog management (add, update, delete products).
  o	Search and filter products based on categories, name, and availability.
  o	Product detail retrieval, including images, descriptions.
4.	Order Service
  o	Create, update, and cancel orders.
  o	Track order status (pending, shipped, delivered, returned).
  o	Handle cart management (add/remove items, calculate totals).
5.	Payment Service
  o	Integration with third-party payment gateways (Stripe, Razorpay).
  o	Payment status management (success, failure, refund).
  o	Support for multiple payment options using gateways (credit card, UPI, net banking).
6.	Notification Service
  o	Send transactional notifications (email, SMS) for events:
    	Order confirmation
    	Payment success/failure
    	Account updates
7.	Service Discovery
  o	Dynamic service registration and discovery using Eureka tools.
  o	Load balancing support with a client-side load balancer.
