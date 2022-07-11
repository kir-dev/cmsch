import ChakraUIRenderer from 'chakra-ui-markdown-renderer'
import { ReactNode } from 'react-markdown/lib/react-markdown'
import { CLIENT_BASE_URL } from '../util/configs/environment.config'
import { CmschLink } from './CmschLink'

const sliceHref = (href: string, pattern: string): string => {
  if (href.indexOf(pattern) === 0) {
    return href.slice(pattern.length, href.length)
  }
  return href
}

const cmschTheme: any = {
  a: (props: { href: string; children: ReactNode }) => {
    const { href, children } = props
    let slicedHref = href
    slicedHref = sliceHref(slicedHref, CLIENT_BASE_URL)
    slicedHref = sliceHref(slicedHref, CLIENT_BASE_URL.replace('https://', ''))
    if (slicedHref.length === 0) slicedHref = '/'
    const isInternal = slicedHref.startsWith('/')
    return (
      <CmschLink isExternal={!isInternal} to={slicedHref}>
        {children}
      </CmschLink>
    )
  }
}

export const CmschUIRenderer = () => ChakraUIRenderer(cmschTheme)