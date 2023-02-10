import { Link } from 'react-router-dom'
import { PropsWithChildren } from 'react'

interface LinkComponentProps extends PropsWithChildren {
  url: string
  external: boolean
}

const LinkComponent = ({ children, url, external }: LinkComponentProps) => {
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
