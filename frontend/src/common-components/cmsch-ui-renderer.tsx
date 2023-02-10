import { ReactNode } from 'react-markdown/lib/react-markdown'
import { CLIENT_BASE_URL } from '../util/configs/environment.config'
import { CmschLink } from './CmschLink'
import { Divider, ListItem, Table, TableContainer, UnorderedList, useColorModeValue } from '@chakra-ui/react'
import ChakraUIRenderer from './chakra-md-renderer'
import { PropsWithChildren } from 'react'

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
    return <Divider my={3} borderColor={useColorModeValue('brand.800', 'brand.200')} borderBottomWidth={2} />
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
