# CMSch client

This project was bootstrapped with
[Create React App](https://github.com/facebook/create-react-app) using template of ChakraUI.

## Development notes

Uses:

- [Chakra UI](https://chakra-ui.com)
- [React-icons](https://react-icons.github.io/react-icons/)
- Typescript 4.5.4

### Basic recommended development workflow

Open IDEA, start the Spring app, but you won't use [localhost:8080](http://localhost:8080) to see your client side
modifications, you don't need to open this url in your browser.

Instead, you open a VSCode in the client project's root (`src/main/client`) and start with

1. `yarn install` to install dependencies locally in node_modules
2. `yarn start` to start React client server

Once the client server is running on preferably [localhost:3000](http://localhost:3000), you need to open this url, where
you can see your frontend modifications in development mode with the React dev-server watching for your code alterations.
Running both of the servers parallel will not trigger CORS related problems, as you are running both of the apps on
your localhost and this is programmed in the backend to be allowed, with the help of this annotation before every controller
used by the frontend:

```kotlin
@CrossOrigin(origins = ["\${g7web.frontend.production-url}"], allowedHeaders = ["*"])
@RestController
@RequestMapping("/api")
class MainController() {  ...  }
```

Never forget the request param out of the paramlist in endpoint methods! Don't forget to include the `g7web.frontend.production-url`
property in the `application-local.properties` file with the value of `http://localhost:3000` (see main README in project root).

As this is a reusable template CMS project, don't forget to set a custom theme of the app in the `utils/customTheme.ts`
file.

### In production mode

The gradle building task will build the react app's files into the static directory of our Spring app, from where the
Spring backend app will serve the built js files, they will access easily the spring app backend through fetch API. This
will not induce CORS related problems, both the backend and these static files will be served in web domain.

If you need to declare a new environment variable, create it in the `src/main/resources/configurations/application.properties`
file and modify the `build.gradle.kts` script (approx. line 97) to include the new property in the built .env file.

When in development mode, you can freely change the .env file. **WARNING**: Gradle build will override the existing .env
file so always have an .env.development file in the client repo as a backup.

## Other notes

### Yarn run tasks

#### `yarn lint`

This will run the eslint and prettier linter and show you your codes faults and where you didn't follow the set clean
code rules in `.eslintrc.json` and `.prettierrc.json`.

#### `yarn fix`

This will not just run the eslint and prettier linter, but will try to fix your faults where you didn't follow the set
clean code rules in `.eslintrc.json` and `.prettierrc.json`. This will not guarantee the full cleanliness of your code.

#### `yarn eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can
`eject` at any time. This command will remove the single build dependency from
your project.

Instead, it will copy all the configuration files and the transitive
dependencies (webpack, Babel, ESLint, etc) right into your project so you have
full control over them. All of the commands except `eject` will still work, but
they will point to the copied scripts so you can tweak them. At this point
you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for
small and middle deployments, and you shouldn’t feel obligated to use this
feature. However we understand that this tool wouldn’t be useful if you couldn’t
customize it when you are ready for it.

### Coding conventions

- Icon pack used: Font Awesome (react-icons)
- Use as many Chakra components as possible minimizing attributes
- Use global theme settings and components to minimize bundle size and unify design
- Color scheme used: 'brand'

### Learn More

You can learn more in the
[Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).
