import { useBrandColor } from '@/util/core-functions.util.ts'
import { Link as RouterLink } from 'react-router'

type Props = {
  isExternal?: boolean
  to: string
  children: React.ReactNode
  className?: string
  style?: React.CSSProperties
}

export const CmschLink = ({ isExternal, to, children, className, style, ...props }: Props) => {
  const color = useBrandColor()
  const Component = (
    <span className={className} style={{ color, ...style }} {...props}>
      {children}
    </span>
  )

  return isExternal ? (
    <a href={to} target="_blank" rel="noreferrer">
      {Component}
    </a>
  ) : (
    <RouterLink to={to}>{Component}</RouterLink>
  )
}
