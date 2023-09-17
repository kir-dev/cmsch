import { Box, Button, Divider, Flex, Grid, Heading, Text, useColorModeValue, useToast, VStack } from '@chakra-ui/react'
import React from 'react'
import { Helmet } from 'react-helmet-async'
import { FaSignInAlt, FaSignOutAlt, FaUndoAlt } from 'react-icons/fa'
import { useNavigate } from 'react-router-dom'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useTeamAcceptJoin } from '../../../api/hooks/team/actions/useTeamAcceptJoin'
import { useTeamCancelJoin } from '../../../api/hooks/team/actions/useTeamCancelJoin'
import { useTeamJoin } from '../../../api/hooks/team/actions/useTeamJoin'
import { useTeamLeave } from '../../../api/hooks/team/actions/useTeamLeave'
import { useTeamMemberKick } from '../../../api/hooks/team/actions/useTeamMemberKick'
import { useTeamPromoteLeadership } from '../../../api/hooks/team/actions/useTeamPromoteLeadership'
import { useTeamRejectJoin } from '../../../api/hooks/team/actions/useTeamRejectJoin'
import { useTeamTogglePermissions } from '../../../api/hooks/team/actions/useTeamTogglePermissions'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable'
import { CmschPage } from '../../../common-components/layout/CmschPage'
import Markdown from '../../../common-components/Markdown'
import { PageStatus } from '../../../common-components/PageStatus'
import { AbsolutePaths } from '../../../util/paths'
import { RoleType } from '../../../util/views/profile.view'
import { TeamResponseMessages, TeamResponses, TeamView } from '../../../util/views/team.view'
import { MemberRow } from './MemberRow'
import { TeamFormItem } from './TeamFormItem'
import { TeamStat } from './TeamStat'
import { TeamTaskCategoryListItem } from './TeamTaskCategoryListItem'

interface TeamDetailsCoreProps {
  team: TeamView | undefined
  isLoading: boolean
  error?: string
  myTeam?: boolean
  admin?: boolean
  refetch?: () => void
}

export function TeamDetailsCore({ team, isLoading, error, myTeam = false, admin = false, refetch = () => {} }: TeamDetailsCoreProps) {
  const toast = useToast()
  const component = useConfigContext()?.components.team
  const navigate = useNavigate()
  const bannerBlanket = useColorModeValue('#FFFFFFAA', '#00000080')

  const actionResponseCallback = (response: TeamResponses) => {
    if (response == TeamResponses.OK) {
      toast({ status: 'success', title: TeamResponseMessages[response] })
      refetch()
    } else {
      toast({ status: 'error', title: TeamResponseMessages[response] })
    }
  }

  const onPutActionSuccess = () => {
    toast({ status: 'success', title: 'Sikeres művelet!' })
    refetch()
  }
  const onPutActionFail = () => {
    toast({ status: 'error', title: 'Sikertelen művelet!' })
  }
  const onPromoteSuccess = () => {
    toast({ status: 'success', title: 'Sikeres művelet!' })
    navigate(AbsolutePaths.MY_TEAM)
  }

  const { joinTeam, joinTeamLoading } = useTeamJoin(actionResponseCallback)
  const { cancelJoin, cancelLoading } = useTeamCancelJoin(actionResponseCallback)
  const { acceptJoin } = useTeamAcceptJoin(onPutActionSuccess, onPutActionFail)
  const { rejectJoin } = useTeamRejectJoin(onPutActionSuccess, onPutActionFail)
  const { togglePermissions } = useTeamTogglePermissions(onPutActionSuccess, onPutActionFail)
  const { promoteLeadership } = useTeamPromoteLeadership(onPromoteSuccess, onPutActionFail)
  const { kickMember } = useTeamMemberKick(onPutActionSuccess, onPutActionFail)
  const { leaveTeam, leaveTeamLoading } = useTeamLeave((response) => {
    actionResponseCallback(response)
    if (response === TeamResponses.OK) navigate(AbsolutePaths.TEAMS)
  })

  if (!component) return <ComponentUnavailable />

  if (error || isLoading || !team) return <PageStatus isLoading={isLoading} isError={!!error} title={component.title} />

  const title = myTeam ? component.myTitle + (admin ? ' (kezelése)' : '') : undefined
  return (
    <CmschPage minRole={admin ? RoleType.PRIVILEGED : myTeam ? RoleType.ATTENDEE : undefined}>
      <Helmet title={title ?? team.name} />
      {title && <Heading mb={5}>{title}</Heading>}
      <Box backgroundImage={team.coverUrl} backgroundPosition="center" backgroundSize="cover" borderRadius="lg" overflow="hidden">
        <Box p={4} bg={bannerBlanket}>
          <Heading fontSize={25} my={0}>
            {team.name}
          </Heading>
          <Text>{team.description}</Text>
        </Box>
      </Box>
      <Flex flex={1} gap={5} justify="space-between" flexDirection={['column', null, 'row']} align="flex-start">
        <VStack align="flex-start" w="full">
          <Grid mt={5} gridAutoRows="auto" w="full" gridTemplateColumns={['full', 'repeat(2, 1fr)', 'repeat(3, 1fr)']} gap={5}>
            {team.stats.map((stat) => (
              <TeamStat key={stat.name} label={stat.name} value={stat.value1} helpText={stat.value2} />
            ))}
          </Grid>
        </VStack>
        <VStack mt={5}>
          {team.joinEnabled && (
            <Button
              leftIcon={<FaSignInAlt />}
              isLoading={joinTeamLoading}
              colorScheme="brand"
              onClick={() => {
                joinTeam(team?.id)
                refetch()
              }}
            >
              Jelentkezés a csapatba
            </Button>
          )}
          {team.joinCancellable && (
            <Button
              leftIcon={<FaUndoAlt />}
              isLoading={cancelLoading}
              variant="outline"
              colorScheme="brand"
              onClick={() => {
                cancelJoin()
                refetch()
              }}
            >
              Jelentkezés visszavonása
            </Button>
          )}
          {team.leaveEnabled && (
            <Button leftIcon={<FaSignOutAlt />} isLoading={leaveTeamLoading} colorScheme="brand" onClick={leaveTeam}>
              Csoport elhagyása
            </Button>
          )}
        </VStack>
      </Flex>
      {team.leaderNotes && (
        <Box mt={5}>
          <Markdown text={team.leaderNotes} />
        </Box>
      )}
      {team.forms && team.forms.length > 0 && (
        <>
          <Divider mt={5} borderWidth={2} />
          <Heading fontSize="lg">Űrlapok</Heading>
          {team.forms.map((category) => (
            <TeamFormItem key={category.name} form={category} />
          ))}
        </>
      )}
      {team.taskCategories && team.taskCategories.length > 0 && (
        <>
          <Divider mt={5} borderWidth={2} />
          <Heading fontSize="lg">Feladatok</Heading>
          {team.taskCategories.map((category) => (
            <TeamTaskCategoryListItem key={category.name} category={category} />
          ))}
        </>
      )}
      {admin && team.applicants?.length > 0 && (
        <>
          <Divider mt={10} borderWidth={2} />
          <Heading fontSize="lg">Jelentkezők</Heading>
          {team.applicants?.map((m) => (
            <MemberRow
              key={m.id}
              member={m}
              onAccept={() => {
                acceptJoin(m.id)
              }}
              onDelete={() => {
                rejectJoin(m.id)
              }}
            />
          ))}
        </>
      )}
      {team.members?.length > 0 && (
        <>
          <Divider mt={5} borderWidth={2} />
          <Heading fontSize="lg">Csapattagok</Heading>
          {team.members?.map((m) => (
            <MemberRow
              key={m.id}
              member={m}
              onPromoteLeadership={
                admin && component?.promoteLeadershipEnabled && !m.admin && !m.you
                  ? () => {
                      promoteLeadership(m.id)
                    }
                  : undefined
              }
              onRoleChange={
                admin && component?.togglePermissionEnabled && !m.you
                  ? () => {
                      togglePermissions(m.id)
                    }
                  : undefined
              }
              onDelete={
                admin && component?.kickEnabled && !m.admin && !m.you
                  ? () => {
                      kickMember(m.id)
                    }
                  : undefined
              }
            />
          ))}
        </>
      )}
    </CmschPage>
  )
}