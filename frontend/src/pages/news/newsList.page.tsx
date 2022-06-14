import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'

const NewsListPage = () => (
  <CmschPage>
    <Helmet />
    <Heading>News list</Heading>
  </CmschPage>
)

export default NewsListPage
