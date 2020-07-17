# Read it 👷  [Work In-Progress] 👷
📢 Have you tried the new Reading experience?

-it is here now with Read it 🙌.

> The simple, intuitive and easy way of reading, share your experience with others by publishing Your thoughts and many more!!

Application Primarily consist of "Articles", You can Follow some "categories" or "Publishers" you trust
and see your home feed filled with many articles related to your interestes with a lot of customization options ✨

## Some Features:
   * Follow categories to get published articles related to those categories
   * Follow specific publishers
   * Publish/write your first articles in simple UI that has a lot features like: extensive list of markups
   * Drafts mode [Uncompleted]
   * Interact with article by doing things like: Appreciate, protest, comment
   * Search
    
Application is still in early stages of development and isn't ready for production yet. if no delay occurs in our schedule, App maybe ready within 1-2 months.

## Implementation:
  * App is written entirely in kotlin except for gradle scripts which is groovy. 
  
  * Application Primarily consist of 4 layers (data, domain, presenter and view). Currently we partially finished data layer: contain most of our business logic like dataSources and Repositories
  * architecture: App is highly inspired by [iosched](https://github.com/google/iosched), it has data and domain layer in separate module and data models also in another separate module
  so architecture in both apps maybe similar However, implementation details aren't the same
  
  * App make use of many of firebase products like: 
     
     1. [Firestore](https://firebase.google.com/docs/firestore): For storing main data, it is the primary storage in app other storage options for storing data that doesn't fit in firestore like: images
     
     2. [Storage](https://firebase.google.com/docs/storage): For storing images (and other binary data in the future)
     
     3. [Authentication](https://firebase.google.com/docs/auth): Firebase-UI particularly For authenticating users 
     
     4. [Analytics](https://firebase.google.com/docs/analytics): For understanding users needs
     
  * Each object From user's perspective represented as model, For example we have a model for Article, Publisher, Category, Comment...etc
  
  * testing: data layer has extensive set of unit tests to ensure every thing is working properly, We use: 
    1. Mockito: For mocking and faking
    2. Robolectric: For tests that need android resources
    3. Truth framework: For assertions 
    
  Also the app uses [ktlint](https://ktlint.github.io/) to enforce certain styles  
    
   **NOTE**: Since i'm the only contributer to this project it is planned to Integrate [CircleCI](https://circleci.com/) later
