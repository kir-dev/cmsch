import { Separator } from '@/components/ui/separator'
import { CLIENT_BASE_URL } from '@/util/configs/environment.config'
import { useBrandColor } from '@/util/core-functions.util.ts'
import type { PropsWithChildren, ReactNode } from 'react'
import type { Components } from 'react-markdown'
import ChakraUIRenderer from './chakra-md-renderer'
import { CmschLink } from './CmschLink'

const sliceHref = (href: string, pattern: string): string => {
  if (href.indexOf(pattern) === 0) {
    return href.slice(pattern.length, href.length)
  }
  return href
}

const cmschTheme: Components = {
  a: (props: { href?: string | undefined; children?: ReactNode }) => {
    const { href, children } = props
    let slicedHref = href || ''
    slicedHref = sliceHref(slicedHref, CLIENT_BASE_URL)
    slicedHref = sliceHref(slicedHref, CLIENT_BASE_URL.replace('https://', ''))
    if (slicedHref.length === 0) slicedHref = '/'
    const isInternal = slicedHref.startsWith('/')
    return (
      <CmschLink isExternal={!isInternal} to={slicedHref}>
        {children}
      </CmschLink>
    )
  },
  ul: ({ children }: PropsWithChildren) => {
    return <ul className="mb-3 pl-5 list-disc">{children}</ul>
  },
  ol: ({ children }: PropsWithChildren) => {
    return <ol className="mb-3 pl-5 list-decimal">{children}</ol>
  },
  li: ({ children }: PropsWithChildren) => {
    return <li className="mb-1">{children}</li>
  },
  hr: () => {
    // eslint-disable-next-line react-hooks/rules-of-hooks
    const color = useBrandColor()
    return <Separator className="my-3 h-0.5" style={{ backgroundColor: color }} />
  },
  table: ({ children }: PropsWithChildren) => {
    return (
      <div className="w-full overflow-auto my-4">
        <table className="w-full border-collapse">{children}</table>
      </div>
    )
  }
}

export const CmschUIRenderer = () => ChakraUIRenderer(cmschTheme)
