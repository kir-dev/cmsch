import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'

const NewsPage = () => (
  <CmschPage>
    <Helmet />
    <Heading>News</Heading>
  </CmschPage>
)

export default NewsPage
