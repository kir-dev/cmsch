import * as React from 'react'
import deepmerge from 'deepmerge'
import { Components } from 'react-markdown'
import {
  chakra,
  Checkbox,
  Code,
  Divider,
  Heading,
  Image,
  Link,
  ListItem,
  OrderedList,
  Table,
  Tbody,
  Td,
  Text,
  Th,
  Thead,
  Tr,
  UnorderedList
} from '@chakra-ui/react'

type GetCoreProps = {
  children?: React.ReactNode
  'data-sourcepos'?: any
}

function getCoreProps(props: GetCoreProps): any {
  return props['data-sourcepos'] ? { 'data-sourcepos': props['data-sourcepos'] } : {}
}

export const defaults: Components = {
  p: (props) => {
    const { children } = props
    return <Text mb={2}>{children}</Text>
  },
  em: (props) => {
    const { children } = props
    return <Text as="em">{children}</Text>
  },
  blockquote: (props) => {
    const { children } = props
    return (
      <Code as="blockquote" p={2}>
        {children}
      </Code>
    )
  },
  code: (props) => {
    const { inline, children, className } = props as any

    if (inline) {
      return <Code p={2} children={children} />
    }

    return <Code className={className} whiteSpace="break-spaces" display="block" w="full" p={2} children={children} />
  },
  del: (props) => {
    const { children } = props
    return <Text as="del">{children}</Text>
  },
  hr: () => {
    return <Divider />
  },
  a: Link,
  img: Image,
  text: (props) => {
    const { children } = props
    return <Text as="span">{children}</Text>
  },
  ul: (props) => {
    const { ordered, children, depth } = props as any
    const attrs = getCoreProps(props)
    let Element = UnorderedList
    let styleType = 'disc'
    if (ordered) {
      Element = OrderedList
      styleType = 'decimal'
    }
    if (depth === 1) styleType = 'circle'
    return (
      <Element spacing={2} as={ordered ? 'ol' : 'ul'} styleType={styleType} pl={4} {...attrs}>
        {children}
      </Element>
    )
  },
  ol: (props) => {
    const { ordered, children, depth } = props as any
    const attrs = getCoreProps(props)
    let Element = UnorderedList
    let styleType = 'disc'
    if (ordered) {
      Element = OrderedList
      styleType = 'decimal'
    }
    if (depth === 1) styleType = 'circle'
    return (
      <Element spacing={2} as={ordered ? 'ol' : 'ul'} styleType={styleType} pl={4} {...attrs}>
        {children}
      </Element>
    )
  },
  li: (props) => {
    const { children, checked } = props as any
    let checkbox = null
    if (checked !== null && checked !== undefined) {
      checkbox = (
        <Checkbox isChecked={checked} isReadOnly>
          {children}
        </Checkbox>
      )
    }
    return (
      <ListItem {...getCoreProps(props)} listStyleType={checked !== null ? 'none' : 'inherit'}>
        {checkbox || children}
      </ListItem>
    )
  },
  h1: (props) => {
    return (
      <Heading my={4} as="h1" variant="main-title" size="2xl" {...getCoreProps(props)}>
        {props.children}
      </Heading>
    )
  },
  h2: (props) => {
    return (
      <Heading my={4} as="h2" size="xl" {...getCoreProps(props)}>
        {props.children}
      </Heading>
    )
  },
  h3: (props) => {
    return (
      <Heading my={4} as="h3" size="lg" {...getCoreProps(props)}>
        {props.children}
      </Heading>
    )
  },
  h4: (props) => {
    return (
      <Heading my={4} as="h4" size="md" {...getCoreProps(props)}>
        {props.children}
      </Heading>
    )
  },
  h5: (props) => {
    return (
      <Heading my={4} as="h5" size="sm" {...getCoreProps(props)}>
        {props.children}
      </Heading>
    )
  },
  h6: (props) => {
    return (
      <Heading my={4} as="h6" size="xs" {...getCoreProps(props)}>
        {props.children}
      </Heading>
    )
  },
  pre: (props) => {
    const { children } = props
    return <chakra.pre {...getCoreProps(props)}>{children}</chakra.pre>
  },
  table: Table,
  thead: Thead,
  tbody: Tbody,
  tr: (props) => <Tr>{props.children}</Tr>,
  td: (props) => <Td>{props.children}</Td>,
  th: (props) => <Th>{props.children}</Th>
}

function ChakraUIRenderer(theme?: Components, merge = true): Components {
  const elements = {
    p: defaults.p,
    em: defaults.em,
    blockquote: defaults.blockquote,
    code: defaults.code,
    del: defaults.del,
    hr: defaults.hr,
    a: defaults.a,
    img: defaults.img,
    text: defaults.text,
    ul: defaults.ul,
    ol: defaults.ol,
    li: defaults.li,
    h1: defaults.h1,
    h2: defaults.h2,
    h3: defaults.h3,
    h4: defaults.h4,
    h5: defaults.h5,
    h6: defaults.h6,
    pre: defaults.pre,
    table: defaults.table,
    thead: defaults.thead,
    tbody: defaults.tbody,
    tr: defaults.tr,
    td: defaults.td,
    th: defaults.th
  }

  if (theme && merge) {
    return deepmerge(elements, theme)
  }

  return elements
}

export default ChakraUIRenderer
