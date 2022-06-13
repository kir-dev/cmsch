import { Heading } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { ExampleComponent } from './components/ExampleComponent'

export const IndexPage = () => (
  <CmschPage>
    <Helmet />
    <Heading size="3xl" textAlign="center" marginTop={10}>
      Üdvözlünk a{' '}
      <Heading as="span" color={useColorModeValue('brand.500', 'brand.600')} size="3xl">
        CMSch
      </Heading>{' '}
      portálon
    </Heading>
    <ExampleComponent />
  </CmschPage>
)
