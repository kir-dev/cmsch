import { Heading } from '@chakra-ui/react'
import { Helmet } from 'react-helmet'
import { CmschPage } from '../../common-components/layout/CmschPage'

const TaskListPage = () => (
  <CmschPage>
    <Helmet />
    <Heading>Task list</Heading>
  </CmschPage>
)

export default TaskListPage
