# Required Features

## Roles

- `User` - Commit to a thread
- `Requester` - open a request, close his request
- `Manager` - manager all threads (ex: mark to `ignored` or `closed`)
- `Admin` - Has the full permissions to configure the Dishub bot

## Definition
### Request
A request can be opened by a user, user can only close his own request.

Each request contains a thread and state
### Thread
A discord channel, which is a part of request.

When message pushed, peoples who subscribed the thread will get a notification

The channel will be locked when the request is closed, and unlocked when request is re-opened.

Manager may delete the whole request which means messages in the thread will be deleted forever.
### Request State
There are three initial states: `opening`, `closed`, `processing`.

Admin can create a state or custom tag, and Manager can add a tag or change the state of a request.

## Commands

### Basic Commands
available for all roles
- `/dashboard`

  A dashboard to do everything without commands

### Request
available for `requester`, `manager` and `admin`
- `/request create`
  
  Create a request


- `/request actions <request>`

  Get available actions of a request, See request action for more 

  
- `/request list [options]`
  
  List all existing requests of current guild

  Filter requests by adding options (ex: Only opening requests)


- `/request subscribe <request>`
  
  Subscribe the request thread


- `/request close <request>`
  
  Close subscription of request thread

### Settings
available for `admin` only 
- `/settings`

  A page to configure settings, it contains:

#### Settings Options

- User Role (default: `@everyone`)
- Requester Role (default: `@everyone`)
- Manager Role (default: `@admin`)
- Admin Role (default: `@admin`)
- Channel Container

  A Category to store thread of requests
- Notification Channel
  
  A channel to mention `manager` and `admin` when new request is opened 