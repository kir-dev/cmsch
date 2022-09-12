import { CmschPage } from '../../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { CustomBreadcrumb } from '../../../common-components/CustomBreadcrumb'
import { Button, Divider, Flex, Heading, Text, useToast, VStack } from '@chakra-ui/react'
import { BoardStat } from '../../../common-components/BoardStat'
import { MemberRow } from './MemberRow'
import { AbsolutePaths } from '../../../util/paths'
import { Loading } from '../../../common-components/Loading'
import { l } from '../../../util/language'
import { Navigate, useNavigate } from 'react-router-dom'
import { TeamResponseMessages, TeamResponses, TeamView } from '../../../util/views/team.view'
import { useServiceContext } from '../../../api/contexts/service/ServiceContext'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useTeamJoin } from '../../../api/hooks/team/actions/useTeamJoin'
import { useTeamLeave } from '../../../api/hooks/team/actions/useTeamLeave'
import { useTeamAcceptJoin } from '../../../api/hooks/team/actions/useTeamAcceptJoin'
import { useTeamRejectJoin } from '../../../api/hooks/team/actions/useTeamRejectJoin'
import { useTeamTogglePermissions } from '../../../api/hooks/team/actions/useTeamTogglePermissions'
import { useTeamMemberKick } from '../../../api/hooks/team/actions/useTeamMemberKick'
import { useTeamCancelJoin } from '../../../api/hooks/team/actions/useTeamCancelJoin'

interface TeamDetailsCoreProps {
  team: TeamView | undefined
  isLoading: boolean
  error?: string
  myTeam?: boolean
  admin?: boolean
  refetch?: () => void
}

export function TeamDetailsCore({ team, isLoading, error, myTeam = false, admin = false, refetch = () => {} }: TeamDetailsCoreProps) {
  const { sendMessage } = useServiceContext()
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

  const { joinTeam, joinTeamLoading } = useTeamJoin(actionResponseCallback)
  const { cancelJoin, cancelLoading } = useTeamCancelJoin(actionResponseCallback)
  const { acceptJoin } = useTeamAcceptJoin(onPutActionSuccess, onPutActionFail)
  const { rejectJoin } = useTeamRejectJoin(onPutActionSuccess, onPutActionFail)
  const { togglePermissions } = useTeamTogglePermissions(onPutActionSuccess, onPutActionFail)
  const { kickMember } = useTeamMemberKick(onPutActionSuccess, onPutActionFail)
  const { leaveTeam, leaveTeamLoading } = useTeamLeave((response) => {
    actionResponseCallback(response)
    if (response === TeamResponses.OK) navigate(AbsolutePaths.TEAMS)
  })

  if (isLoading) {
    return <Loading />
  }

  if (error) {
    sendMessage(l('team-load-failed') + error)
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }

  if (typeof team === 'undefined' || !component) {
    sendMessage(l('team-load-failed-contact-developers'))
    return <Navigate replace to={AbsolutePaths.ERROR} />
  }
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
    <CmschPage>
      <Helmet title={title} />
      <CustomBreadcrumb items={breadcrumbItems} mt={5} />
      <Flex justify="space-between" wrap="wrap">
        <VStack align="flex-start">
          <Heading>{title}</Heading>
          {(myTeam || admin) && <Text>{team.name}</Text>}
          {component.showTeamScore && <BoardStat label="Csapat pont" value={team.points} />}
        </VStack>
        <VStack mt={10}>
          {team.joinEnabled && (
            <Button isLoading={joinTeamLoading} colorScheme="brand" onClick={() => joinTeam(team?.id)}>
              Jelentkezés a csapatba
            </Button>
          )}
          {team.joinCancellable && (
            <Button isLoading={cancelLoading} variant="outline" colorScheme="brand" onClick={cancelJoin}>
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
              onRoleChange={
                admin
                  ? () => {
                      togglePermissions(m.id)
                    }
                  : undefined
              }
              onDelete={
                admin
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
