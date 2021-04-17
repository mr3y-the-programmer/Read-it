# Read it 👷  [Work In-Progress] 👷

[![mr3y-the-programmer](https://circleci.com/gh/mr3y-the-programmer/Read-it.svg?style=svg&circle-token=7f43838ebd42698f4247a19d80e6cc2ab03e85cb)](https://app.circleci.com/pipelines/github/mr3y-the-programmer/Read-it)

Android app to read, write & publish articles similar to [Medium.com](https://medium.com/) or [dev.to](https://dev.to/) with using Firebase as a backend for the app,
it is mostly a challange to see how firebase can be used in real large-scale apps like Medium.com

## Some Features:
   * Follow categories to get published articles related to those categories
   * Follow specific publishers
   * write/publish articles
   * Drafts mode [Uncompleted]
   * Interact with article by: Appreciating, disagreeing or commenting
   * Search [Early development] (Implemented using [Algolia](https://www.algolia.com/) client)
 
## TODO: 
   * Implement the UI
   * Complete Drafts mode
   * Benchmark the hotpaths Using [AndroidX Jetpack Benchmark](https://developer.android.com/studio/profile/benchmark)

## Tech Stack:
Some of technologies used in this app

| Tool        | For           |
| ------------- |:-------------:|
| Firestore, Authentication, Cloud Storage     | backend |
| Firebase Remote config, Firebase Analytics | controlling, testing & Analyzing User flow |
| Kotlin coroutines & Flow    | Concurrency & building Reactive Usecases   |
| JUnit 4, mockito-kotlin, Truth | testing |
| Paging 3 | split the data into chunks (pages) |
| CircleCi | CI/CD |

## License:
   ```
   Copyright [2020] [MR3Y]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   ```
