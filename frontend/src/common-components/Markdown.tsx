import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { CmschUIRenderer } from './cmsch-ui-renderer'

type Props = {
  text?: string
}

const Markdown = ({ text }: Props) => {
  return <ReactMarkdown components={CmschUIRenderer()} children={text || ''} remarkPlugins={[remarkGfm]} />
}

export default Markdown
