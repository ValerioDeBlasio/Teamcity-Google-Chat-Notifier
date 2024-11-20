## Teamcity Google Workspace Chat Notifier

This plugin is responsible for sending notifications of started, completed or failed builds via Google Workspace Chat spaces.
The behavior is very similar to notifications sent via Slack.

### Usage
To use the plugin first you have to create a webhook on the Google Space you want to use.  
`Go to space, Apps & integrations, Add Webhook` and save the generated webhook.

Now go to `Teamcity -> profile -> Notification Rules -> Google Chat Notifier`, then set the Google Chat Thread (any value will do) and the Google Chat Webhook.

Then set which events you want to monitor.

### Customization

Within **GoogleChatWrapper.java** you can customize the message that is sent as a notification.

By default, a message contains:
- Project
- Build
- Build Number
- Event
 
example  
**Build start notification:**  
`Project / Build Develop[refs/heads/develop] #739 Build Start ✔`

**Failure notification:**  
`Project / Build Develop[refs/heads/develop] #739 Build Failed: Exit code 1 (Step: run test (Command Line)) (new) ❌`

### Build
To build the plugin use the command `mvn package`, inside the target folder you will find a file named **GoogleChatNotifier.zip**
 

### Install
To install the plugin go to `teamcity Administration -> plugin -> upload plugin zip`.


