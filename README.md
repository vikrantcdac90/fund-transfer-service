# fund-transfer-service

# Porject Name : Fund Transfer Service

# Swagger URL : http://localhost:8080/swagger-ui/index.html

# Accounts API Documentation

# Base URL  http://localhost:8080/api/accounts

Endpoints Details 

# GET All Accounts

Example URL : http://localhost:8080/api/accounts

Description: Retrieves all accounts.

Response: Returns a list of all accounts with their details.

Sample Response:

{
    "success": true,
    "message": "Accounts fetched successfully",
    "data": [
        {
            "ownerId": "9c232936-3d6e-47c3-9ec7-34392e6c54a0",
            "currency": "EUR",
            "balance": 100.00
        }
    ]
}


# GET By UUID

Example URL : http://localhost:8080/api/accounts/0b5d9f2f-23fb-4fb3-b9b8-1e22c9b24451

Description: Retrieves an account by its ID.

Path Parameters: {id} UUID of the account to retrieve.

Response: Returns details of the account corresponding to the provided ID.

Sample Response:

{
    "success": true,
    "message": "Accounts fetched successfully",
    "data": [
        {
            "ownerId": "9c232936-3d6e-47c3-9ec7-34392e6c54a0",
            "currency": "EUR",
            "balance": 100.00
        }
    ]
}

# POST /api/accounts/create

Example URL:  http://localhost:8080/api/accounts/create

Description: Creates a new account.

Request Body: AccountDto object representing the account details.

Sample Request Body:

{
    "currency": "USD",
    "balance": 1000.00
}

Response: Returns the created account details.

Sample Response:

{
    "success": true,
    "message": "account created successfully",
    "data": {
        "ownerId": "a3801546-6f51-48f8-ad69-16c33f0d6a3a",
        "currency": "USD",
        "balance": 1000.00
    }
}


# DELETE /api/accounts/{id}

Description: Deletes an account by its ID.

Path Parameters: {id} UUID of the account to delete.

Response: Returns a success message upon successful deletion.

Sample Response:

{
    "success": true,
    "message": "Account deleted successfully",
    "data": "account delete success"
}

# Transfer amount 

Description: transfer amount from one owner id to another

Endpoint : POST /api/accounts/transfer

Response: Returns a success message upon successful transfer.

Sample payload to send a transfer request:

{
    "fromOwnerId": "a3801546-6f51-48f8-ad69-16c33f0d6a3a",
    "toOwnerId": "b3801546-6f51-48f8-ad69-16c33f0d6a3b",
    "amount": 600.00
}



# Use the following JSON data for testing the API operations:
create account with usd as currency
{
    "currency": "USD",
    "balance": 1000.00
}


create account with eur as currrency
{
    "currency": "EUR",
    "balance": 600.00
}

fund transfer payload :

{
    "fromOwnerId": "a3801546-6f51-48f8-ad69-16c33f0d6a3a",
    "toOwnerId": "b3801546-6f51-48f8-ad69-16c33f0d6a3b",
    "amount": 600.00
}


feel free to Contact for any issue : +352-661376222 (vikrant)
