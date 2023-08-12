# TodoApp

This repository contains a small web application that comprises a Pedestal service and a database connection for persisting todo items. These two components are integrated into a system using Stuart Sierra's Component library.

## Usage

To run the production code, you'll need a PostgreSQL database. You can create a suitable database with the following command:

    docker run --rm -e POSTGRES_PASSWORD=clojure -e POSTGRES_USER=clojure -e POSTGRES_DB=clojure -p 5432:5432 postgres:10-alpine

After setting up the database, start the system by running:

    lein run

Once the system is up and running, the service can be accessed at http://localhost:8082.

## Code explanation
### project.clj

This Clojure project definition outlines the key attributes and dependencies of the "clojure-todo-app" project:

Project Name: clojure-todo-app
Dependencies: A list of project dependencies, including libraries like Clojure, Component, Hiccup, Java JDBC, PostgreSQL driver, Korma, Pedestal, and various logging libraries.
Main Namespace: The main namespace of the project, set to clojure-todo-app.system.
Target Path: The directory where compiled output (Uberjar) will be placed, specified as "target/%s".
Profiles: Custom profiles for different build scenarios. The uberjar profile compiles all namespaces ahead of time (AOT) and specifies JVM options.
Minimum Leiningen Version: 2.0.0. The minimum version of Leiningen required to build and manage the project.
Resource Paths: Additional paths for resources, including configuration files and other project-related assets.
Overall, this project definition provides a foundation for developing a Clojure-based web application with specified dependencies and configurations.

### system.clj

This Clojure namespace clojure-todo-app.system defines a system configuration for a web application using the Component library and Pedestal framework:

Importing Required Libraries: The namespace imports necessary libraries, including com.stuartsierra.component, io.pedestal.http, and various components of the todo app.

build-system-map: A private function that takes an environment env and constructs a system map with configuration options for the Pedestal server. It includes routing, server type (Jetty), port (8082), resource path for static files, and a flag for not joining the server yet.

db-config: A map containing database configuration parameters such as database name, user, and password.

system: A function that builds the complete system configuration using Component. It includes the build-system-map output, db-config, a PostgreSQL database component created using clojure-todo-app.components.db.postgres/new-database, and a Pedestal server component created using clojure-todo-app.components.web.pedestal/new-pedestal. The components are wired together using component/using.

-main: The entry point of the application. It starts the system using component/start.

Commented Code: Demonstrates how to start and stop the system manually using Component.

Overall, this namespace sets up a system configuration that combines a Pedestal web server and a PostgreSQL database component using the Component library, enabling you to build and manage the web application's lifecycle.

### routes.clj

This Clojure namespace clojure-todo-app.components.web.routes defines routes and URL generation for a Pedestal web application:

Importing Required Libraries: The namespace imports various libraries including io.pedestal.http, io.pedestal.http.body-params, clojure-todo-app.components.web.index, and io.pedestal.http.route.

common-interceptors: A vector of common interceptors to be applied to routes. It includes body-params/body-params (for handling body parameters) and http/html-body (for handling HTML responses).

routes: A set of route definitions using a set literal (#{}). Each route definition is a vector containing the following components:

The route pattern: e.g., "/" or "/task/add".
The HTTP method: :get or :post.
Interceptors to apply, including common-interceptors and specific interceptors from index namespace.
A route name: e.g., :index, :task-add, etc.
url-for: A function that generates URLs for routes defined in the routes set. It uses route/url-for-routes and route/expand-routes from the io.pedestal.http.route library to create URLs based on route names and parameters.

In summary, this namespace sets up route definitions for the Pedestal web application and provides a utility function url-for to generate URLs for these routes. The routes include patterns, HTTP methods, interceptors, and names to handle different actions in the web application.

### index.clj

This Clojure namespace clojure-todo-app.components.web.index provides the implementation for rendering the main web page of a Todo application. It includes functions to generate HTML components, handle form submissions, and interact with the tasks component to perform various actions. Here's an overview of the namespace:

Importing Required Libraries: The namespace imports ring.util.response for generating HTTP responses, hiccup.page for HTML rendering, and clojure-todo-app.components.web.tasks for interacting with tasks.

toggle-task-index and delete-task-index: These private functions generate HTML forms for toggling and deleting tasks, respectively. They create <form> elements with appropriate attributes and input fields.

tasks->rows: This function generates HTML rows representing tasks. It uses a for comprehension to iterate through sorted tasks and create a table row for each task. Each row includes columns for the task title, status (done/undone), toggle button, and delete button.

base-template: This function generates the base HTML template for the web page. It includes a <head> section with necessary metadata and CSS, and a <body> section with a container for the page content.

home-page: The main function that generates the home page of the Todo application. It uses the base-template and constructs the page with a form for adding tasks, a list of tasks with toggle and delete buttons, and a "Hello, world!" message.

add-task, toggle-task, and delete-task: These functions handle form submissions for adding, toggling, and deleting tasks, respectively. They interact with the tasks component and perform the corresponding actions before redirecting back to the home page.

respond-hello: A simple function that responds with a "Hello, world!" message. It's not directly used in the main application but is included for demonstration purposes.

In summary, this namespace handles the rendering of the main web page, form submissions, and interactions with tasks in the Todo application. It uses Hiccup for generating HTML and Pedestal's routing and interceptors for handling HTTP requests and responses.

### postgres.clj


This Clojure namespace clojure-todo-app.components.db.postgres provides the implementation for managing the PostgreSQL database component of the Todo application. It includes functions and a record for creating, starting, and stopping the database connection, as well as interacting with the tasks component. Here's an overview of the namespace:

Importing Required Libraries: The namespace imports com.stuartsierra.component for component lifecycle management, korma.db for database interactions, and clojure-todo-app.components.web.tasks to interact with tasks.

Postgres Record: This defrecord defines the Postgres component, which implements the component/Lifecycle protocol. It has two fields: db-config, which holds the database configuration, and database, which holds the active database connection.

start: Initializes the database connection using the provided db-config. It also sets the default Korma connection to the created database and calls the create-table! function from the tasks component to ensure the necessary table exists.

stop: Closes the database connection by setting the default Korma connection to nil.

new-database: A function that returns an instance of the Postgres component. It takes no arguments and simply creates an instance of the Postgres record with an empty map.

In summary, this namespace encapsulates the functionality for managing the PostgreSQL database component of the Todo application. It uses Stuart Sierra's Component library to handle the lifecycle of the database connection and interacts with Korma for database operations. The Postgres component ensures that the database connection is properly started and stopped, and that the necessary table is created before usage.

### tasks.clj

This Clojure namespace clojure-todo-app.components.web.tasks defines functions and entity mappings to interact with the tasks stored in the database. Here's a breakdown of the code:

Importing Required Libraries: The namespace imports korma.core to work with database operations.

Entity Mapping: The kc/defentity macro defines an entity named task and specifies its table name and entity fields (id, title, and done). This creates a mapping between the Clojure entity and the corresponding database table.

create-table!: This function executes a raw SQL query to create the tasks table if it doesn't already exist. It defines the columns id, title, and done.

drop-table!: This function executes a raw SQL query to drop the tasks table if it exists.

clear-table!: This function uses the kc/delete function to delete all rows from the task entity, effectively clearing the tasks table.

add!: This function uses the kc/insert function to add a new task to the tasks table. It takes a title parameter and inserts a new row with the specified title.

delete!: This function executes a raw SQL query to delete a task by its id from the tasks table.

toggle!: This function executes a raw SQL query to toggle the done status of a task by its id in the tasks table.

query-all: This function uses the kc/select function to retrieve all tasks from the task entity, effectively querying all rows from the tasks table.

Commented Code: The commented code block at the end demonstrates how to use these functions, including creating the table, adding a task, deleting a task by ID, and clearing the table.

In summary, this namespace encapsulates the database operations related to tasks for the Todo application. It provides functions to create, modify, and retrieve tasks in the underlying database.

### pedestal.clj

This Clojure namespace clojure-todo-app.components.web.pedestal defines a component that manages the Pedestal HTTP server used in the web application. Here's a breakdown of the code:

Importing Required Libraries: The namespace imports com.stuartsierra.component to work with components and io.pedestal.http for Pedestal-specific functionality.

test? Function: This function takes a system-map and checks whether the environment is set to :test. It returns true if the environment is :test, indicating that the application is running in a testing environment.

Pedestal Record: This record defines a Pedestal component. It implements the component/Lifecycle protocol, which specifies how the component should start and stop.

start: The start method creates and starts the Pedestal HTTP server if it hasn't been started already. It checks the system field and the environment to determine whether to start the server. If system is already set (indicating it was created outside the component), the server is not created or started. If the environment is not a test environment, the HTTP server is started.

stop: The stop method stops the Pedestal HTTP server if it was created and started by the component. It also clears the system field.

new-pedestal Function: This function creates a new instance of the Pedestal record, initializing it with an empty map.

In summary, this namespace encapsulates the creation and management of the Pedestal HTTP server component for the web application. It ensures that the server is started and stopped appropriately based on the environment and the component's lifecycle.
