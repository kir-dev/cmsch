/* eslint-disable @typescript-eslint/no-explicit-any */
import { Checkbox } from '@/components/ui/checkbox'
import { Separator } from '@/components/ui/separator'
import deepmerge from 'deepmerge'
import * as React from 'react'
import type { Components } from 'react-markdown'

type GetCoreProps = {
  children?: React.ReactNode
  'data-sourcepos'?: any
}

function getCoreProps(props: GetCoreProps): any {
  return props['data-sourcepos'] ? { 'data-sourcepos': props['data-sourcepos'] } : {}
}

// eslint-disable-next-line react-refresh/only-export-components
export const defaults: Components = {
  p: (props) => {
    const { children } = props
    return (
      <p className="mb-2" {...getCoreProps(props)}>
        {children}
      </p>
    )
  },
  em: (props) => {
    const { children } = props
    return <em {...getCoreProps(props)}>{children}</em>
  },
  blockquote: (props) => {
    const { children } = props
    return (
      <blockquote className="border-l-4 border-gray-300 pl-4 italic my-4" {...getCoreProps(props)}>
        {children}
      </blockquote>
    )
  },
  code: (props) => {
    const { inline, children, className } = props as any

    if (inline) {
      return (
        <code className="bg-muted px-1.5 py-0.5 rounded-sm text-sm" {...getCoreProps(props)}>
          {children}
        </code>
      )
    }

    return (
      <pre className="bg-muted p-4 rounded-md overflow-x-auto my-4" {...getCoreProps(props)}>
        <code className={className}>{children}</code>
      </pre>
    )
  },
  del: (props) => {
    const { children } = props
    return <del {...getCoreProps(props)}>{children}</del>
  },
  hr: () => {
    return <Separator className="my-4" />
  },
  a: (props) => <a className="text-primary hover:underline" {...props} />,
  img: (props) => <img className="max-w-full h-auto rounded-lg my-4" {...props} />,
  text: (props) => {
    const { children } = props
    return <span {...getCoreProps(props)}>{children}</span>
  },
  ul: (props) => {
    const { children } = props as any
    return (
      <ul className="list-disc pl-6 mb-4 space-y-1" {...getCoreProps(props)}>
        {children}
      </ul>
    )
  },
  ol: (props) => {
    const { children } = props as any
    return (
      <ol className="list-decimal pl-6 mb-4 space-y-1" {...getCoreProps(props)}>
        {children}
      </ol>
    )
  },
  li: (props) => {
    const { children, checked } = props as any
    if (checked !== null && checked !== undefined) {
      return (
        <li className="flex items-center space-x-2 mb-1" {...getCoreProps(props)}>
          <Checkbox checked={checked} disabled />
          <span>{children}</span>
        </li>
      )
    }
    return (
      <li className="mb-1" {...getCoreProps(props)}>
        {children}
      </li>
    )
  },
  h1: (props) => {
    return (
      <h1 className="text-4xl font-bold font-heading my-6" {...getCoreProps(props)}>
        {props.children}
      </h1>
    )
  },
  h2: (props) => {
    return (
      <h2 className="text-3xl font-bold font-heading my-5" {...getCoreProps(props)}>
        {props.children}
      </h2>
    )
  },
  h3: (props) => {
    return (
      <h3 className="text-2xl font-bold font-heading my-4" {...getCoreProps(props)}>
        {props.children}
      </h3>
    )
  },
  h4: (props) => {
    return (
      <h4 className="text-xl font-bold font-heading my-3" {...getCoreProps(props)}>
        {props.children}
      </h4>
    )
  },
  h5: (props) => {
    return (
      <h5 className="text-lg font-bold font-heading my-2" {...getCoreProps(props)}>
        {props.children}
      </h5>
    )
  },
  h6: (props) => {
    return (
      <h6 className="text-base font-bold font-heading my-2" {...getCoreProps(props)}>
        {props.children}
      </h6>
    )
  },
  pre: (props) => {
    const { children } = props
    return (
      <pre className="whitespace-pre-wrap" {...getCoreProps(props)}>
        {children}
      </pre>
    )
  },
  table: (props) => (
    <div className="w-full overflow-x-auto my-4">
      <table className="w-full border-collapse border border-border" {...props} />
    </div>
  ),
  thead: (props) => <thead className="bg-muted" {...props} />,
  tbody: (props) => <tbody {...props} />,
  tr: (props) => <tr className="border-b border-border" {...props} />,
  td: (props) => <td className="p-2 border border-border" {...props} />,
  th: (props) => <th className="p-2 border border-border font-bold text-left" {...props} />
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
