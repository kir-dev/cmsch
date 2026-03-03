import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useTeamAcceptJoin } from '@/api/hooks/team/actions/useTeamAcceptJoin'
import { useTeamCancelJoin } from '@/api/hooks/team/actions/useTeamCancelJoin'
import { useTeamJoin } from '@/api/hooks/team/actions/useTeamJoin'
import { useTeamLeave } from '@/api/hooks/team/actions/useTeamLeave'
import { useTeamMemberKick } from '@/api/hooks/team/actions/useTeamMemberKick'
import { useTeamPromoteLeadership } from '@/api/hooks/team/actions/useTeamPromoteLeadership'
import { useTeamRejectJoin } from '@/api/hooks/team/actions/useTeamRejectJoin'
import { useTeamTogglePermissions } from '@/api/hooks/team/actions/useTeamTogglePermissions'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { useToast } from '@/hooks/use-toast'
import { joinPath } from '@/util/core-functions.util'
import { AbsolutePaths, Paths } from '@/util/paths'
import { RoleType, RoleTypeString } from '@/util/views/profile.view'
import { TeamResponseMessages, TeamResponses, type TeamView } from '@/util/views/team.view'
import { LogIn, LogOut, Undo2 } from 'lucide-react'
import { useState } from 'react'
import { useNavigate } from 'react-router'
import { MemberRow } from './MemberRow'
import { TeamFormItem } from './TeamFormItem'
import TeamLabel from './TeamLabel.tsx'
import { TeamStat } from './TeamStat'
import { TeamTaskCategoryListItem } from './TeamTaskCategoryListItem'

interface TeamDetailsCoreProps {
  team: TeamView | undefined
  isLoading: boolean
  error?: string
  myTeam?: boolean
  refetch?: () => void
}

export function TeamDetailsCore({ team, isLoading, error, myTeam = false, refetch = () => {} }: TeamDetailsCoreProps) {
  const { toast } = useToast()
  const config = useConfigContext()
  const components = config?.components
  const teamComponent = components?.team
  const raceComponent = components?.race
  const userRole = config?.role
  const navigate = useNavigate()
  const [isEditingMembers, setIsEditingMembers] = useState(false)
  const isUserGroupAdmin = RoleType[userRole || RoleTypeString.GUEST] >= RoleType.PRIVILEGED

  const actionResponseCallback = (response: TeamResponses) => {
    if (response == TeamResponses.OK) {
      toast({ title: TeamResponseMessages[response] })
      refetch()
    } else {
      toast({ variant: 'destructive', title: TeamResponseMessages[response] })
    }
  }

  const onPutActionSuccess = () => {
    toast({ title: 'Sikeres művelet!' })
    refetch()
  }
  const onPutActionFail = () => {
    toast({ variant: 'destructive', title: 'Sikertelen művelet!' })
  }
  const onPromoteSuccess = () => {
    toast({ title: 'Sikeres művelet!' })
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
    <CmschPage minRole={myTeam ? RoleType.ATTENDEE : undefined} title={title ?? team.name}>
      {title && <h1 className="text-3xl font-bold font-heading mb-5">{title}</h1>}
      <div
        className="bg-center bg-cover rounded-lg overflow-hidden relative"
        style={{ backgroundImage: team.coverUrl ? `url(${team.coverUrl})` : undefined }}
      >
        <div className="p-4 flex flex-col md:flex-row justify-between bg-background/60 dark:bg-background/50">
          <div className="pb-4">
            <h2 className="text-2xl font-bold my-0">{team.name}</h2>
            <div className="flex flex-wrap gap-2 pt-2">
              {team.labels &&
                team.labels.map((label, index) => (
                  <div key={index}>
                    <TeamLabel name={label.name} color={label.color} desc={label.description} />
                  </div>
                ))}
            </div>
            <p className="mt-2">{team.description}</p>
          </div>
          <div>
            {team.logo && <img className="max-w-[128px] max-h-[128px] rounded-md object-contain" src={team.logo} alt="Csapat logó" />}
          </div>
        </div>
      </div>
      {team.descriptionRejected && (
        <div
          className="mt-5 bg-center bg-cover rounded-lg overflow-hidden relative"
          style={{ backgroundImage: team.coverUrl ? `url(${team.coverUrl})` : undefined }}
        >
          <div className="p-4 bg-background/60 dark:bg-background/50">
            <h2 className="text-2xl font-bold my-0">A csapat leírása elutasításra került</h2>
            <p>Az adminisztrátor üzenete: {team.descriptionRejectionReason ?? ''}</p>
          </div>
        </div>
      )}
      <div className="flex flex-col md:flex-row flex-1 gap-5 justify-between items-start">
        <div className="w-full">
          <div className="mt-5 grid w-full grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5">
            {team.stats.map((stat) => (
              <TeamStat key={stat.name} stat={stat} />
            ))}
          </div>
        </div>
        <div className="flex flex-col space-y-2 mt-5">
          {team.joinEnabled && (
            <Button
              className="flex items-center gap-2 bg-primary text-primary-foreground"
              disabled={joinTeamLoading}
              onClick={() => {
                joinTeam(team?.id)
                refetch()
              }}
            >
              <LogIn className="h-4 w-4" /> Jelentkezés a csapatba
            </Button>
          )}
          {team.joinCancellable && (
            <Button
              disabled={cancelLoading}
              variant="outline"
              className="flex items-center gap-2 text-destructive border-destructive hover:bg-destructive/10"
              onClick={() => {
                cancelJoin()
                refetch()
              }}
            >
              <Undo2 className="h-4 w-4" /> Jelentkezés visszavonása
            </Button>
          )}
          {team.leaveEnabled && (
            <Button className="flex items-center gap-2" disabled={leaveTeamLoading} variant="destructive" onClick={leaveTeam}>
              <LogOut className="h-4 w-4" /> Csoport elhagyása
            </Button>
          )}
          {teamComponent.showRaceButton && (
            <LinkButton href={joinPath(AbsolutePaths.TEAMS, 'details', team.id, Paths.RACE)} className="bg-primary text-primary-foreground">
              {raceComponent?.title ?? 'Verseny'} eredmények
            </LinkButton>
          )}
        </div>
      </div>
      {team.leaderNotes && (
        <div className="mt-5">
          <Markdown text={team.leaderNotes} />
        </div>
      )}
      {teamComponent.showAdvertisedForms && team.forms && team.forms.length > 0 && (
        <>
          <Separator className="mt-5 h-px bg-border" />
          <h3 className="text-xl font-bold mt-2">Űrlapok</h3>
          {team.forms.map((category) => (
            <TeamFormItem key={category.name} form={category} />
          ))}
        </>
      )}
      {teamComponent.showTasks && team.taskCategories && team.taskCategories.length > 0 && (
        <>
          <Separator className="mt-5 h-px bg-border" />
          <h3 className="text-xl font-bold mt-2">Feladatok</h3>
          {team.taskCategories.map((category) => (
            <TeamTaskCategoryListItem key={category.name} category={category} />
          ))}
        </>
      )}
      {team.applicants?.length > 0 && (
        <>
          <Separator className="mt-10 h-px bg-border" />
          <h3 className="text-xl font-bold mt-2">Jelentkezők</h3>
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
          <Separator className="my-5 h-px bg-border" />
          <div className="flex items-center justify-between flex-wrap gap-4">
            <h3 className="m-0 text-xl font-bold">Csapattagok</h3>
            {isUserGroupAdmin && myTeam && (
              <div className="flex flex-wrap gap-4">
                <Button variant={isEditingMembers ? 'outline' : 'default'} onClick={() => setIsEditingMembers((prev) => !prev)}>
                  {isEditingMembers ? 'Szerkesztés befejezése' : 'Tagok szerkesztése'}
                </Button>
                {teamComponent.teamEditEnabled && <LinkButton href={AbsolutePaths.EDIT_TEAM}>Csoport adatok szerkesztése</LinkButton>}
              </div>
            )}
          </div>
          {team.members?.map((m) => (
            <MemberRow
              key={m.id}
              member={m}
              onPromoteLeadership={
                isEditingMembers && teamComponent?.promoteLeadershipEnabled && !m.isAdmin && !m.isYou
                  ? () => {
                      promoteLeadership(m.id)
                    }
                  : undefined
              }
              onRoleChange={
                isEditingMembers && teamComponent?.togglePermissionEnabled && !m.isYou
                  ? () => {
                      togglePermissions(m.id)
                    }
                  : undefined
              }
              onDelete={
                isEditingMembers && teamComponent?.kickEnabled && !m.isAdmin && !m.isYou
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
