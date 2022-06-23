import { Skeleton } from '@chakra-ui/react'
import { CmschPage } from '../../../common-components/layout/CmschPage'

export const TaskDetailsSkeleton = () => {
  return (
    <CmschPage loginRequired groupRequired>
      <Skeleton h="3rem" mt={5} />
      <Skeleton h={5} mt={5} />
      <Skeleton h={5} mt={5} />
      <Skeleton h={5} mt={5} />
    </CmschPage>
  )
}
