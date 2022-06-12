import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'

export const TaskListPage = () => (
  <CmschPage>
    <Helmet />
    <Heading>Task list</Heading>
  </CmschPage>
)
