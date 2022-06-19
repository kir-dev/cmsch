import { Link } from 'react-router-dom'
import { HasChildren } from '../../util/react-types.util'

type Props = {
  url: string
  external: boolean
} & HasChildren

const LinkComponent = ({ children, url, external }: Props) => {
  return external ? (
    <a href={url} target="_blank" referrerPolicy="no-referrer">
      {children}
    </a>
  ) : (
    <Link to={url || '#'} className={children ? undefined : 'navitem'}>
      {children}
    </Link>
  )
}

export default LinkComponent
