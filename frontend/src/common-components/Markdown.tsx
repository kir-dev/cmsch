import { Separator } from '@/components/ui/separator.tsx'
import { CLIENT_BASE_URL } from '@/util/configs/environment.config.ts'
import type { ReactNode } from 'react'
import type { Components } from 'react-markdown'
import ReactMarkdown from 'react-markdown'
import { Link } from 'react-router'
import remarkGfm from 'remark-gfm'

const baseUrlNoProto = CLIENT_BASE_URL.replace(/^[a-z]+:\/\//, '')

const markdownComponents: Components = {
  a: (props: { href?: string | undefined; children?: ReactNode }) => {
    const { href, children } = props

    let path = href
    if (path?.startsWith(CLIENT_BASE_URL)) {
      path = path.slice(CLIENT_BASE_URL.length)
    } else if (path?.startsWith(baseUrlNoProto)) {
      path = path.slice(baseUrlNoProto.length)
    }

    path = path || '/'

    if (path.startsWith('/')) {
      return (
        <Link className="text-primary hover:underline" to={path}>
          {children}
        </Link>
      )
    }
    return (
      <a className="text-primary hover:underline" href={path} target="_blank" rel="noreferrer">
        {children}
      </a>
    )
  },
  p: (props) => <p className="mb-2">{props.children}</p>,
  em: (props) => <em>{props.children}</em>,
  blockquote: (props) => <blockquote className="border-l-4 border-gray-300 pl-4 italic my-4">{props.children}</blockquote>,
  code: (props) => (
    <pre className="bg-muted p-4 rounded-md overflow-x-auto my-4">
      <code className={props.className}>{props.children}</code>
    </pre>
  ),
  del: (props) => <del>{props.children}</del>,
  hr: () => <Separator className="my-4" />,
  img: (props) => <img className="max-w-full h-auto rounded-lg my-4" {...props} />,
  text: (props) => <span>{props.children}</span>,
  ul: (props) => <ul className="list-disc pl-6 mb-4 space-y-1">{props.children}</ul>,
  ol: (props) => <ol className="list-decimal pl-6 mb-4 space-y-1">{props.children}</ol>,
  li: (props) => <li className="mb-1">{props.children}</li>,
  h1: (props) => <h1 className="text-4xl font-bold font-heading my-6">{props.children}</h1>,
  h2: (props) => <h2 className="text-3xl font-bold font-heading my-5">{props.children}</h2>,
  h3: (props) => <h3 className="text-2xl font-bold font-heading my-4">{props.children}</h3>,
  h4: (props) => <h4 className="text-xl font-bold font-heading my-3">{props.children}</h4>,
  h5: (props) => <h5 className="text-lg font-bold font-heading my-2">{props.children}</h5>,
  h6: (props) => <h6 className="text-base font-bold font-heading my-2">{props.children}</h6>,
  pre: (props) => <pre className="whitespace-pre-wrap">{props.children}</pre>,
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

const Markdown = ({ text }: { text?: string }) => {
  if (!text) return null
  return <ReactMarkdown components={markdownComponents} children={text} remarkPlugins={[remarkGfm]} />
}

export default Markdown
