package commands

import bjda.plugins.supercommand.command
import bjda.utils.message
import variables.RequestState

val HelpCommand = command("help", "Learn how to use Dishub for your Community") {
    val help = message {
        embed(
            title = "Getting Started",
            description = """
                    Dishub only support slash commands, it provided a Request System to management Tasks.
                    Please checked if the bot is missing Admin Permission, the admin permission is required for the bot.
                    
                    **Create Request**
                    `/request create`
                    This command will open a Modal, you can give your request a Title, Description and Tags.
                    After creating it, you will see the thread of request.
                    
                    **Updating Requests**
                    The request owner can edit the Request title and description,
                    Only Managers can change request state and tags
                    
                    **State**
                    ${
                        RequestState.values().joinToString(separator = "\n") { 
                            "${it.emoji.formatted} **${it.name}** ${it.description}"
                        }
                    }
                    
                    **Note**
                    A manager can change request state and tags, it is a role that replying to members' requests.
                    Request thread is a private Text Channel, only subscribed members can view the channel.
                    
                    **List Requests**
                    To see all existing request, use `/request list`,
                    You can also filter requests by adding options.
                    
                    **Subscribe a request**
                    In default, request threads are private, only peoples who subscribed to it can view them.
                    
                    You have to use `/request open` to open and subscribe the request thread.
                    The Request Id is same as the id you get from `/request list`
                    
                    **Unsubscribe Request**
                    To unsubscribe a request, you can use `/request close`
                     
                    **Delete Request**
                    `/request delete`
                    We don't recommend you to delete the request, the message history will be lost after deleting the request.
                    
                    **Settings**
                    `/settings container` - Set the Category to store Request Threads
                    `/settings manager` - Set the Role of Manager
                    
                    **Utils**
                    `/thread reference` - Reference a Request in any channels
                    `/thread nav` - A Navigate Bar to jump to the top or bottom of message history.
                    `/todo create` - Create a Todo List, stored for each user
                """.trimIndent()
        )
    }

    execute {
        event.reply(help.build()).queue()
    }
}
