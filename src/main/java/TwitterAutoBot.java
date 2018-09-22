import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class TwitterAutoBot {

    /** 
     * 
     * @param args 
     */
    public static void main(String[] args) {
        //tweetLines();
        getTimeline();
        displayUserInfo();
        //sendTweet("Hello everybody");
    }

    /** 
     * Tweets messages from tweets.txt every 30 minutes through the connected user. 
     */
    private static void tweetLines() {
        String line;
        try {
            try (
                    InputStream fis = new FileInputStream("C:\\Prog\\Projects\\TwitterAutoBot\\src\\main\\Resources\\Tweets.txt"); // file path for text file containing tweets
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("Cp1252"));
                    BufferedReader br = new BufferedReader(isr);
            ) {
                while ((line = br.readLine()) != null) {
                    // Deal with the line
                    sendTweet(line);
                    System.out.println("Tweeting: " + line + ".");

                    try {
                        System.out.println("Sleeping for 30 minutes...");
                        Thread.sleep(1800000); // every 30 minutes
                        // Thread.sleep(10000); // every 10 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /** 
     * Sends a tweet through the connected user
     * @param line = string to be posted as a tweet
     */
    private static void sendTweet(String line) {
        // Ensures that the tweet that the bot is trying to post is not too long to fit as one message.
        // Possible that TwitterException handles this as well. 
        if (line.length() > 256) {
            System.out.println("The message is too long to be posted as one tweet.");
            return;
        }
        
        Twitter twitter = TwitterFactory.getSingleton();
        Status status;
        try {
            status = twitter.updateStatus(line);
            System.out.println(status);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simple function which returns the home timeline of the connected user, and outputs it to a text file using FileOutputStream. 
     * Currently oly displays the first ~7 tweets on the TL.
     */
    private static void getTimeline() {
        
        String message;
            
        try {
            Twitter twitter = TwitterFactory.getSingleton();
            User user = twitter.verifyCredentials();
            
            try {
                try (FileOutputStream fop = new FileOutputStream("C:\\Prog\\Projects\\TwitterAutoBot\\src\\main\\Resources\\timeline.txt")) {
                    List<Status> statuses = twitter.getHomeTimeline();
                    System.out.println("Showing home timeline of " + user.getName() + " (@" + user.getScreenName() + "): \n");
                    for (Status status : statuses) {
                        message = status.getText();
                        System.out.println(status.getUser().getName() + ": " + message);
                        byte [] statusInBytes = (message + "\n").getBytes(); //adds line break to each tweet, and converts it to bytes in order to write it to file
                        fop.write(statusInBytes);
                        fop.flush();
                    }   }
            
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        } catch (TwitterException e) { 
            e.printStackTrace();
            System.out.println("Failed to get timeline: " + e.getMessage());
        }
    }
    
    /**
     * Displays info about the user which is currently connected to the bot. 
     */
    private static void displayUserInfo() {
        try {
            Twitter twitter = TwitterFactory.getSingleton();
            User user = twitter.verifyCredentials();
            
            System.out.println("Current user: \n");
            System.out.println("User ID:             " + user.getId());
            System.out.println("User name:           " + user.getName());
            System.out.println("User screen name:    " + user.getScreenName());
            System.out.println("User description:    " + user.getDescription());
            System.out.println("User follower count: " + user.getFollowersCount());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

}