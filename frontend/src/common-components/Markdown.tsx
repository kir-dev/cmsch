import ReactMarkdown from 'react-markdown'

const Markdown = ({ text }: { text: string | undefined }) => {
  return <ReactMarkdown children={text || ''} />
}

export default Markdown
