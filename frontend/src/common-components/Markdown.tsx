import ReactMarkdown from 'react-markdown'
import { Paragraph } from './Paragraph'

const Markdown = ({ text }: { text: string | undefined }) => {
  return (
    <Paragraph>
      <ReactMarkdown children={text || ''} />
    </Paragraph>
  )
}

export default Markdown
