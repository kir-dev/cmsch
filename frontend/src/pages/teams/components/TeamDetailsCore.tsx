import { Box, Button, Divider, Flex, Grid, Heading, Image, Text, useColorModeValue, useToast, VStack } from '@chakra-ui/react'
import React, { useState } from 'react'
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
import { LinkButton } from '../../../common-components/LinkButton'
import Markdown from '../../../common-components/Markdown'
import { PageStatus } from '../../../common-components/PageStatus'
import { joinPath } from '../../../util/core-functions.util'
import { AbsolutePaths, Paths } from '../../../util/paths'
import { RoleType } from '../../../util/views/profile.view'
import { TeamResponseMessages, TeamResponses, TeamView } from '../../../util/views/team.view'
import { MemberRow } from './MemberRow'
import { TeamFormItem } from './TeamFormItem'
import { TeamStat } from './TeamStat'
import { TeamTaskCategoryListItem } from './TeamTaskCategoryListItem'
import { API_BASE_URL } from '../../../util/configs/environment.config'

interface TeamDetailsCoreProps {
  team: TeamView | undefined
  isLoading: boolean
  error?: string
  myTeam?: boolean
  refetch?: () => void
}

export function TeamDetailsCore({ team, isLoading, error, myTeam = false, refetch = () => {} }: TeamDetailsCoreProps) {
  const toast = useToast()
  const { components } = useConfigContext()
  const teamComponent = components.team
  const raceComponent = components.race
  const userRole = useConfigContext().role
  const navigate = useNavigate()
  const bannerBlanket = useColorModeValue('#FFFFFFAA', '#00000080')
  const [isEditingMembers, setIsEditingMembers] = useState(false)
  const isUserGroupAdmin = RoleType[userRole] >= RoleType.PRIVILEGED

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

  if (!teamComponent) return <ComponentUnavailable />

  if (error || isLoading || !team) return <PageStatus isLoading={isLoading} isError={!!error} title={teamComponent.title} />
  const title = myTeam ? teamComponent.myTitle : undefined
  return (
    <CmschPage minRole={myTeam ? RoleType.ATTENDEE : undefined}>
      <Helmet title={title ?? team.name} />
      {title && <Heading mb={5}>{title}</Heading>}
      <Box backgroundImage={team.coverUrl} backgroundPosition="center" backgroundSize="cover" borderRadius="lg" overflow="hidden">
        <Box p={4} bg={bannerBlanket} display="flex" justifyContent="space-between" flexDirection={{ base: 'column', md: 'row' }}>
          <Box pb={4}>
            <Heading fontSize={25} my={0}>
              {team.name}
            </Heading>
            <Text>{team.description}</Text>
          </Box>
          <Box>
            {team.logo && (
              <Image
                maxW={'md'}
                maxH={'md'}
                maxWidth="100%"
                src={`${API_BASE_URL}/cdn/team/${team.logo}`}
                alt="Csapat logó"
                borderRadius="md"
              />
            )}
          </Box>
        </Box>
      </Box>
      <Flex flex={1} gap={5} justify="space-between" flexDirection={['column', null, 'row']} align="flex-start">
        <VStack align="flex-start" w="full">
          <Grid mt={5} gridAutoRows="auto" w="full" gridTemplateColumns={['full', 'repeat(2, 1fr)', 'repeat(3, 1fr)']} gap={5}>
            {team.stats.map((stat) => (
              <TeamStat key={stat.name} stat={stat} />
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
              colorScheme="red"
              onClick={() => {
                cancelJoin()
                refetch()
              }}
            >
              Jelentkezés visszavonása
            </Button>
          )}
          {team.leaveEnabled && (
            <Button leftIcon={<FaSignOutAlt />} isLoading={leaveTeamLoading} colorScheme="red" onClick={leaveTeam}>
              Csoport elhagyása
            </Button>
          )}
          {teamComponent.showRaceButton && (
            <LinkButton href={joinPath(AbsolutePaths.TEAMS, 'details', team.id, Paths.RACE)} ml={5} colorScheme="brand">
              {raceComponent?.title ?? 'Verseny'} eredmények
            </LinkButton>
          )}
        </VStack>
      </Flex>
      {team.leaderNotes && (
        <Box mt={5}>
          <Markdown text={team.leaderNotes} />
        </Box>
      )}
      {teamComponent.showAdvertisedForms && team.forms && team.forms.length > 0 && (
        <>
          <Divider mt={5} borderWidth={2} />
          <Heading fontSize="lg">Űrlapok</Heading>
          {team.forms.map((category) => (
            <TeamFormItem key={category.name} form={category} />
          ))}
        </>
      )}
      {teamComponent.showTasks && team.taskCategories && team.taskCategories.length > 0 && (
        <>
          <Divider mt={5} borderWidth={2} />
          <Heading fontSize="lg">Feladatok</Heading>
          {team.taskCategories.map((category) => (
            <TeamTaskCategoryListItem key={category.name} category={category} />
          ))}
        </>
      )}
      {team.applicants?.length > 0 && (
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
          <Divider my={5} borderWidth={2} />
          <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={4}>
            <Heading m={0} fontSize="lg">
              Csapattagok
            </Heading>
            {isUserGroupAdmin && myTeam && (
              <Box display="flex" flexWrap="wrap" gap={4}>
                <Button variant={isEditingMembers ? 'outline' : 'solid'} onClick={() => setIsEditingMembers((prev) => !prev)}>
                  {isEditingMembers ? 'Szerkesztés befejezése' : 'Tagok szerkesztése'}
                </Button>
                {teamComponent.teamEditEnabled && (
                  <LinkButton variant="solid" href={AbsolutePaths.EDIT_TEAM}>
                    Csoport adatok szerkesztése
                  </LinkButton>
                )}
              </Box>
            )}
          </Box>
          {team.members?.map((m) => (
            <MemberRow
              key={m.id}
              member={m}
              onPromoteLeadership={
                isEditingMembers && teamComponent?.promoteLeadershipEnabled && !m.admin && !m.you
                  ? () => {
                      promoteLeadership(m.id)
                    }
                  : undefined
              }
              onRoleChange={
                isEditingMembers && teamComponent?.togglePermissionEnabled && !m.you
                  ? () => {
                      togglePermissions(m.id)
                    }
                  : undefined
              }
              onDelete={
                isEditingMembers && teamComponent?.kickEnabled && !m.admin && !m.you
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
