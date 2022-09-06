import { Tab, useColorModeValue } from '@chakra-ui/react'
import { HasChildren } from '../../../util/react-types.util'

export const CustomTab = ({ children }: HasChildren) => {
  const styles = {
    color: useColorModeValue('brand.500', 'brand.200'),
    borderRadius: '5px 5px 0 0',
    borderBottomColor: useColorModeValue('brand.500', 'brand.500'),
    borderBottomWidth: '2px'
  }
  const selectedStyles = {
    color: 'white',
    bg: useColorModeValue('brand.500', 'brand.500')
  }
  return (
    <Tab _selected={selectedStyles} style={styles}>
      {children}
    </Tab>
  )
}
