# Capture-Data-Change
A module that captures all the changes that happens on a sql server database and then convert each change to a json object and save it in the database to be sent later on.

Introduction to CDC (Change data capture):
Change data capture records insert, update, and delete activity that is applied to a SQL Server table. This makes the details of the changes available in an easily consumed relational format. Column information and the metadata that is required to apply the changes to a target environment is captured for the modified rows and stored in change tables that mirror the column structure of the tracked source tables.

All objects that are associated with a capture instance are created in the change data capture schema of the enabled database. By default, the name is <schema name_table name> of the source table. Its associated change table is named by appending _CT to the capture instance name. 

The first five columns of a change data capture change table are metadata columns. These provide additional information that is relevant to the recorded change. The remaining columns mirror the identified captured columns from the source table in name and, typically, in type. These columns hold the captured column data that is gathered from the source table.

Each insert or delete operation that is applied to a source table appears as a single row within the change table. The data columns of the row that results from an insert operation contain the column values after the insert. The data columns of the row that results from a delete operation contain the column values before the delete. An update operation requires one row entry to identify the column values before the update, and a second row entry to identify the column values after the update.

Each row in a change table also contains additional metadata to allow interpretation of the change activity. The column __$start_lsn identifies the commit log sequence number (LSN) that was assigned to the change.

 The column __$operation records the operation that is associated with the change: 1 = delete, 2 = insert, 3 = update (before image), and 4 = update (after image).

Client side:
A scheduler runs every 3 hours that loops on each table in the change data capture schema and takes all records with insert, update(after) and delete operations. Each record is then composed into a json object that will be sent later on to the server by another scheduler.

Each json object carries all columns’ data of a single record of the captured instance along with the distribution company id, table name and the operation number. Also, Each Json Object has a successful send date that will only be set when it is sent and processed successfully at the server side.

For each captured instance, the scheduler saves the __$start_lsn of the last record to be used as a starting point for the next time the scheduler runs.

Another scheduler runs every 24 hours that takes all json objects that has not been sent yet and sends them to the server.

The scheduler sends the data on batches and never sends a new batch unless it receives a success message from the server. In case it didn’t receive a success message from the server for any reason, it logs the error and tries to send the same batch again for a number of times.

When the scheduler receives a success message from the server it sets the successful send date for each json object sent in the batch with the current date and log the sent batch.

When the scheduler finishes sending all the batches, it clears all the sent data from the database.
