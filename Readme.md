# TODO API

Project source link - https://roadmap.sh/projects/todo-list-api

A small application to maintain todo tasks. An authenticated user will be able to add tasks, update, delete and list all the tasks.

1. A user can register.
2. A user can login.
3. Authenticated user can access APIs.
4. Only authorized users will be able to update or delete tasks. (only the person created the task will be able to upadte/delete it)


## Usage

### How to register?

Sample API call - 

Make a call to the end point - "http://localhost:9090/register" 

With the JSON body like - 

{
"name": "Bhargav",
"email": "bhargav@example.com",
"password": "securePassword123"
}

### How to login?

Sample API call -

Make a call to the end point - "http://localhost:9090/login"

With the JSON body like -

{
"email": "bhargav@example.com",
"password": "securePassword123"
}

NOTE - A JWT token will be generated, save it.

## Access the task related APIs


### Add Task API

Sample API call -

Make a call to the end point - "http://localhost:9090/todo/task"

With the JSON body like -

{
"name": "Complete API Integration",
"description": "Integrate the new authentication service with the API"
}


NOTE -  Include Authrizaton, Bearer <token> in the request.

Ouput will be the created task with details including id and createdBy.

### Update Task API

Sample API call -

Make a call to the end point - "http://localhost:9090/todo/update/1"

With the JSON body like -

{
"name": "Complete API Integration-updated",
"description": "Integrate the new authentication service with the API-updated"
}

NOTE -  Include Authrizaton, Bearer <token> in the request.

Ouput will be the created updated task with details including id and createdBy.

### Delete Task API

Sample API call -

Make a call to the end point - "http://localhost:9090/todo/delete/1"

NOTE -  Include Authrizaton, Bearer <token> in the request.

### Get Tasks API

Sample API call -

Make a call to the end point - "http://localhost:9090/todo"

NOTE -  Include Authrizaton, Bearer <token> in the request.

Output will be list of all tasks.
