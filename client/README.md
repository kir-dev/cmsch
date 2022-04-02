# CMSch client

This project was bootstrapped with
[Create React App](https://github.com/facebook/create-react-app) using template of ChakraUI.

## Development notes

Uses:

- [Chakra UI](https://chakra-ui.com)
- [React-icons](https://react-icons.github.io/react-icons/)
- Typescript 4.5.4

Your AuthSCH client should use `http://localhost:3000/auth/success` as the redirect URL.

See other development notes in the root README.md file.

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
