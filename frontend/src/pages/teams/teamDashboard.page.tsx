import { Box, Grid, Heading, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { useMemo } from 'react'
import { Helmet } from 'react-helmet-async'
import { FaHashtag, FaMedal, FaUsers } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useTeamDashboard } from '../../api/hooks/team/queries/useTeamDashboard'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { PageStatus } from '../../common-components/PageStatus'
import { RoleType } from '../../util/views/profile.view'
import { TaskCategoryListItem } from '../task/components/TaskCategoryListIem'
import { TeamStat } from './components/TeamStat'

export default function TeamDashboardPage() {
  const bannerBlanket = useColorModeValue('#FFFFFFAA', '#00000080')
  const component = useConfigContext()?.components.team
  const taskComponent = useConfigContext()?.components.task
  const { data, isLoading, error } = useTeamDashboard()

  const maxBucketListCount = useMemo(() => {
    if (!data?.task) return 0
    return data.task.categories.reduce((acc, curr) => acc + curr.sum, 0)
  }, [data?.task])

  const solvedBucketListCount = useMemo(() => {
    if (!data?.task) return 0
    return data.task.categories.reduce((acc, curr) => acc + curr.approved, 0)
  }, [data?.task])

  if (!component) return <ComponentUnavailable />

  if (error || isLoading || !data) return <PageStatus isLoading={isLoading} isError={!!error} title={component.title} />

  return (
    <CmschPage minRole={RoleType.ATTENDEE} loginRequired>
      <Helmet title={data.teamName} />
      <Box backgroundImage={data.bannerUrl} backgroundPosition="center" backgroundSize="cover" borderRadius="lg" overflow="hidden">
        <Box p={4} bg={bannerBlanket}>
          <Heading fontSize={25} my={0}>
            {data.teamName}
          </Heading>
          <Text>{data.creatorName} által létrehozva</Text>
        </Box>
      </Box>
      <Grid mt={5} gridAutoRows="auto" gridTemplateColumns={['1fr', 'repeat(2, 1fr)', 'repeat(3, 1fr)']} gap={5}>
        <TeamStat label="Pontszám" value={data.points} helpText="pont" icon={FaHashtag} />
        <TeamStat label="Helyezés" value={data.leaderboardPlace} helpText="hely" icon={FaMedal} />
        <TeamStat label="Létszám" value={data.memberCount} helpText="tag" icon={FaUsers} />
        <TeamStat label="Riddle" value={data.riddles.solvedCount} maxValue={data.riddles.maxCount} helpText="megoldva" />
        <TeamStat label="Bucketlist" value={solvedBucketListCount} maxValue={maxBucketListCount} helpText="elfogadva" />
      </Grid>
      <VStack spacing={4} mt={5} align="stretch">
        <Heading>{taskComponent?.title}</Heading>
        {data.task.categories.map((category) => (
          <TaskCategoryListItem key={category.categoryId} category={category} />
        ))}
      </VStack>
    </CmschPage>
  )
}
