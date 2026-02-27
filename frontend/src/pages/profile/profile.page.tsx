import { useAuthContext } from '@/api/contexts/auth/useAuthContext'
import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useProfileQuery } from '@/api/hooks/profile/useProfileQuery.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import { LinkButton } from '@/common-components/LinkButton'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { PresenceAlert } from '@/common-components/PresenceAlert'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { API_BASE_URL } from '@/util/configs/environment.config'
import { AbsolutePaths } from '@/util/paths'
import templateStringReplace from '@/util/templateStringReplace'
import { GuildType, RoleType } from '@/util/views/profile.view'
import { AlertCircle, CheckCircle2, Info } from 'lucide-react'
import React, { useEffect } from 'react'
import { Link, Navigate, useNavigate } from 'react-router'
import { GroupLeaderContactList } from './components/GroupLeaderContactList'
import { ProfileQR } from './components/ProfileQR'
import { completedPercent, submittedPercent } from './util/percentFunctions'

const Map = React.lazy(() => import('@/common-components/map/GroupMapContainer'))

const CircularProgress = ({
  value,
  value2 = 0,
  size = 160,
  strokeWidth = 10,
  label,
  label2,
  color = 'text-success',
  color2 = 'text-warning'
}: {
  value: number
  value2?: number
  size?: number
  strokeWidth?: number
  label: string
  label2?: string
  color: string
  color2?: string
}) => {
  const radius = (size - strokeWidth) / 2
  const circumference = 2 * Math.PI * radius
  const offset = circumference - (value / 100) * circumference
  const offset2 = circumference - ((value + value2) / 100) * circumference

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      <svg className="h-full w-full -rotate-90 transform" width={size} height={size}>
        <circle
          className="stroke-border"
          strokeWidth={strokeWidth}
          stroke="currentColor"
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
        />
        {value2 > 0 && (
          <circle
            className={color2 + ' stroke-current'}
            strokeWidth={strokeWidth}
            strokeDasharray={circumference}
            style={{ strokeDashoffset: offset2 }}
            strokeLinecap="round"
            stroke="currentColor"
            fill="transparent"
            r={radius}
            cx={size / 2}
            cy={size / 2}
          />
        )}
        <circle
          className={color + ' stroke-current'}
          strokeWidth={strokeWidth}
          strokeDasharray={circumference}
          style={{ strokeDashoffset: offset }}
          strokeLinecap="round"
          stroke="currentColor"
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
        <span className={label2 ? 'translate-y-[-0.5rem]' : ''}>{label}</span>
        {label2 && (
          <>
            <div className="h-px w-2/3 bg-border my-1" />
            <span className="translate-y-[0.5rem]">{label2}</span>
          </>
        )}
      </div>
    </div>
  )
}

const ProfilePage = () => {
  const { onLogout } = useAuthContext()
  const { isLoading: profileLoading, data: profile, error: profileError, refetch } = useProfileQuery()
  const navigate = useNavigate()

  useEffect(() => {
    const savedPath = sessionStorage.getItem('path')
    if (savedPath) {
      sessionStorage.removeItem('path')
      navigate(savedPath)
    }
    refetch()
  }, [navigate, refetch])

  const config = useConfigContext()?.components
  const component = config?.profile

  if (!component) return <ComponentUnavailable />
  if (profileError || profileLoading || !profile) return <PageStatus isLoading={profileLoading} isError={!!profileError} title="Profil" />

  if (!profile.loggedIn || profile.role === 'GUEST') {
    return <Navigate replace to="/" />
  }

  const raceStats = profile?.raceStats

  return (
    <CmschPage loginRequired={true} title={component?.title}>
      {component.messageBoxContent && (
        <Alert className="mt-5 border-l-4">
          <Info className="h-4 w-4" />
          <AlertDescription>{component.messageBoxContent}</AlertDescription>
        </Alert>
      )}
      <PresenceAlert acquired={profile?.collectedTokenCount || 0} needed={profile?.minTokenToComplete || 0} className="mt-5" />

      {component?.showIncompleteProfile && (
        <Alert variant={profile.profileIsComplete ? 'default' : 'destructive'} className="mb-5 border-l-4">
          {profile.profileIsComplete ? <CheckCircle2 className="h-4 w-4" /> : <AlertCircle className="h-4 w-4" />}
          <div className="flex w-full flex-wrap items-center justify-between">
            <div className="py-2">
              {profile.profileIsComplete
                ? component.profileComplete
                : templateStringReplace(component.profileIncomplete, profile.incompleteTasks?.join(', '))}
            </div>
            {!profile.profileIsComplete && !!config.task && (
              <LinkButton href={AbsolutePaths.TASKS} className="ml-5">
                {config.task.title}
              </LinkButton>
            )}
          </div>
        </Alert>
      )}

      <div className="flex flex-col justify-between md:flex-row">
        <div>
          {component.showFullName && <h1 className="text-4xl font-bold tracking-tight mb-2">{profile.fullName}</h1>}
          {component.showAlias && <p className="text-xl">Becenév: {profile.alias || 'nincs'}</p>}
          {component.showNeptun && <p className="text-xl">Neptun: {profile.neptun || 'nincs'}</p>}
          {component.showEmail && <p className="text-xl">E-mail: {profile.email || 'nincs'}</p>}

          {component.showGuild && <p className="text-xl">Gárda: {GuildType[profile?.guild || 'UNKNOWN'] || 'nincs'}</p>}
          {component.showMajor && <p className="text-xl">Szak: {profile.major || 'nincs'}</p>}
          {component.showGroup && (
            <p className="text-xl">
              {component?.groupTitle}: {profile.groupName || 'nincs'}
            </p>
          )}
        </div>
        <div className="mt-5 flex flex-col gap-2 md:mt-0 md:items-stretch">
          {profile.role && RoleType[profile.role] >= RoleType.STAFF && (
            <LinkButton href={`${API_BASE_URL}/admin/control`} external>
              Admin panel
            </LinkButton>
          )}
          {config?.groupselection && profile.groupSelectionAllowed && (
            <LinkButton href={AbsolutePaths.CHANGE_GROUP}>{component?.groupTitle} módosítása</LinkButton>
          )}
          {component.aliasChangeEnabled && <LinkButton href={AbsolutePaths.CHANGE_ALIAS}>Becenév módosítása</LinkButton>}
          <Button variant="outline" onClick={onLogout}>
            Kijelentkezés
          </Button>
        </div>
      </div>

      {component.showGroup && <GroupLeaderContactList profile={profile} />}

      {profile.userMessage && (
        <>
          <Separator className="my-8 h-1" />
          <Markdown text={profile.userMessage} />
        </>
      )}

      {profile.groupMessage && (
        <>
          <Separator className="my-8 h-1" />
          <Markdown text={profile.groupMessage} />
        </>
      )}

      {component.showRaceStats && raceStats && (
        <>
          <h2 className="pb-2 text-center text-xl font-bold underline">Sörmérés</h2>

          <p className="text-center font-bold">{`Legjobb idő: ${raceStats.bestTime}s`}</p>
          <p className="pb-2 text-center font-bold italic">{`${raceStats.placement}. helyezett`}</p>

          <div className="grid grid-cols-2 gap-x-5">
            <p className="text-right italic">Mérések száma:</p>
            <p>{` ${raceStats.timesParticipated}`}</p>

            <p className="text-right italic">Átlag idő:</p>
            <p>{`${raceStats.averageTime}s`}</p>

            <p className="text-right italic">Szórás:</p>
            <p>{`${raceStats.deviation}s`}</p>

            <p className="text-right italic">Kalória/másodperc:</p>
            <p>{`${raceStats.kCaloriesPerSecond} kcal/s`}</p>
          </div>
        </>
      )}

      {component.showQr && profile.cmschId && (
        <>
          <Separator className="my-8 h-1" />
          <ProfileQR profile={profile} component={component} />
        </>
      )}
      {(component.showTasks || component.showRiddles || component.showTokens) && <Separator className="my-8 h-1" />}

      <div className="flex flex-wrap items-center justify-center">
        {component.showTasks && (
          <div className="p-3">
            <div className="flex flex-col items-center">
              <Link to={AbsolutePaths.TASKS} className="text-3xl font-medium hover:underline hover:text-primary">
                {component.taskCounterName}
              </Link>
              <CircularProgress
                value={completedPercent(profile)}
                value2={submittedPercent(profile)}
                label={`${Math.round(completedPercent(profile))}%`}
                label2={submittedPercent(profile) !== 0 ? `${Math.round(submittedPercent(profile))}%` : undefined}
                color="text-success"
                color2="text-warning"
              />
            </div>
          </div>
        )}

        {component.showRiddles && (
          <div className="p-3">
            <div className="flex flex-col items-center">
              <Link to={AbsolutePaths.RIDDLE} className="text-3xl font-medium hover:underline hover:text-primary">
                {component.riddleCounterName}
              </Link>
              <CircularProgress
                value={((profile?.completedRiddleCount || 0) / (profile?.totalRiddleCount || 0)) * 100}
                label={`${Math.round(((profile?.completedRiddleCount || 0) / (profile?.totalRiddleCount || 0)) * 100)}%`}
                color="text-success"
              />
            </div>
          </div>
        )}

        {component.showTokens && (
          <div className="p-3">
            <div className="flex flex-col items-center">
              <Link to={AbsolutePaths.TOKEN} className="text-3xl font-medium hover:underline hover:text-primary">
                {component.tokenCounterName}
              </Link>
              <CircularProgress
                value={((profile?.collectedTokenCount || 0) / (profile?.totalTokenCount || 0)) * 100}
                label={`${Math.round(((profile?.collectedTokenCount || 0) / (profile?.totalTokenCount || 0)) * 100)}%`}
                color="text-success"
              />
            </div>
          </div>
        )}
      </div>
      {component.showGroupLeadersLocations && <Map />}
    </CmschPage>
  )
}

export default ProfilePage
