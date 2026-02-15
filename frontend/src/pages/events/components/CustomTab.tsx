import { Tab } from '@chakra-ui/react'
import type { PropsWithChildren } from 'react'
import { useBrandColor } from '../../../util/core-functions.util.ts'

export const CustomTab = ({ children }: PropsWithChildren) => {
  const styles = {
    color: useBrandColor(500, 200),
    borderRadius: '5px 5px 0 0',
    borderBottomColor: useBrandColor(500, 500),
    borderBottomWidth: '2px'
  }
  const selectedStyles = {
    color: 'white',
    bg: useBrandColor(500, 500)
  }
  return (
    <Tab px={2} _selected={selectedStyles} style={styles}>
      {children}
    </Tab>
  )
}
