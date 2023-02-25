import { CmschPage } from '../../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { CustomBreadcrumb } from '../../../common-components/CustomBreadcrumb'
import { Button, Divider, Flex, Heading, Text, useToast, VStack } from '@chakra-ui/react'
import { BoardStat } from '../../../common-components/BoardStat'
import { MemberRow } from './MemberRow'
import { AbsolutePaths } from '../../../util/paths'
import { useNavigate } from 'react-router-dom'
import { TeamResponseMessages, TeamResponses, TeamView } from '../../../util/views/team.view'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useTeamJoin } from '../../../api/hooks/team/actions/useTeamJoin'
import { useTeamLeave } from '../../../api/hooks/team/actions/useTeamLeave'
import { useTeamAcceptJoin } from '../../../api/hooks/team/actions/useTeamAcceptJoin'
import { useTeamRejectJoin } from '../../../api/hooks/team/actions/useTeamRejectJoin'
import { useTeamTogglePermissions } from '../../../api/hooks/team/actions/useTeamTogglePermissions'
import { useTeamMemberKick } from '../../../api/hooks/team/actions/useTeamMemberKick'
import { useTeamCancelJoin } from '../../../api/hooks/team/actions/useTeamCancelJoin'
import { RoleType } from '../../../util/views/profile.view'
import { useTeamPromoteLeadership } from '../../../api/hooks/team/actions/useTeamPromoteLeadership'
import { ComponentUnavailable } from '../../../common-components/ComponentUnavailable'
import { PageStatus } from '../../../common-components/PageStatus'

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

  const breadcrumbItems = [
    {
      title: component.title,
      to: AbsolutePaths.TEAMS
    },
    {
      title: team.name
    }
  ]
  const title = admin ? component.myTitle + ' kezelése' : myTeam ? component.myTitle : team.name
  return (
    <CmschPage minRole={admin ? RoleType.PRIVILEGED : myTeam ? RoleType.ATTENDEE : undefined}>
      <Helmet title={title} />
      <CustomBreadcrumb items={breadcrumbItems} mt={5} />
      <Flex justify="space-between" wrap="wrap">
        <VStack align="flex-start">
          <Heading>{title}</Heading>
          {(myTeam || admin) && <Text>{team.name}</Text>}
          <Flex wrap="wrap" gap={3}>
            {team.stats.map((stat) => (
              <BoardStat label={stat.name} value={stat.value1} subValue={stat.value2} navigateTo={stat.navigate} />
            ))}
          </Flex>
        </VStack>
        <VStack mt={10}>
          {team.joinEnabled && (
            <Button
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
            <Button isLoading={leaveTeamLoading} colorScheme="brand" onClick={leaveTeam}>
              Csoport elhagyása
            </Button>
          )}
        </VStack>
      </Flex>
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
          <Divider mt={10} borderWidth={2} />
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
