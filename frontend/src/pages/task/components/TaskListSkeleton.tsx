import { Box, Heading, Skeleton, Stack } from '@chakra-ui/react'
import { CmschPage } from '../../../common-components/layout/CmschPage'

type TaskSkeletonProps = {
  height: string
  title?: string
}

export const TaskSkeleton = ({ height, title }: TaskSkeletonProps) => {
  return (
    <CmschPage loginRequired groupRequired>
      {title ? <Heading>{title}</Heading> : <Box h={15} />}
      <Stack mt={5}>
        <Skeleton height={height} />
        <Skeleton height={height} />
        <Skeleton height={height} />
      </Stack>
    </CmschPage>
  )
}
