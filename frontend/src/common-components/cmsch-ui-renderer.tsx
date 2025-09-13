import { Divider, ListItem, Table, TableContainer, UnorderedList } from '@chakra-ui/react'
import { PropsWithChildren, ReactNode } from 'react'
import { Components } from 'react-markdown'
import { CLIENT_BASE_URL } from '../util/configs/environment.config'
import { useBrandColor } from '../util/core-functions.util.ts'
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
    return (
      <UnorderedList mb={3} pl={3}>
        {children}
      </UnorderedList>
    )
  },
  li: ({ children }: PropsWithChildren) => {
    return <ListItem>{children}</ListItem>
  },
  hr: () => {
    // eslint-disable-next-line react-hooks/rules-of-hooks
    return <Divider my={3} borderColor={useBrandColor(800, 200)} borderBottomWidth={2} />
  },
  table: ({ children }: PropsWithChildren) => {
    return (
      <TableContainer>
        <Table>{children}</Table>
      </TableContainer>
    )
  }
}

export const CmschUIRenderer = () => ChakraUIRenderer(cmschTheme)
