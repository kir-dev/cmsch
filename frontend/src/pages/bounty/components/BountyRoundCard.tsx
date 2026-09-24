import type { Bounty } from '@/api/contexts/config/types.ts'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card.tsx'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useTimeInSeconds } from '@/hooks/useDate'
import { stringifyTimeStamp, TIMESTAMP_OPTIONS } from '@/util/core-functions.util'
import type { BountyRegistrationView, BountyRoundView, BountyTeamView } from '@/util/views/bounty.view'
import { Crosshair, Flag, Skull, Sword } from 'lucide-react'
import { useState } from 'react'
import QRCode from 'react-qr-code'
import { KillDialog } from './KillDialog'

const formatRemaining = (seconds: number): string => {
  if (seconds <= 0) return 'lejárt'
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  if (days > 0) return `${days} nap ${hours} óra`
  if (hours > 0) return `${hours} óra ${minutes} perc`
  return `${minutes} perc`
}

export const BountyRoundCard = ({ round, component }: { round: BountyRoundView; component: Bounty }) => {
  const now = useTimeInSeconds(1000)
  const [killDialogOpen, setKillDialogOpen] = useState(false)
  const [qrOpen, setQrOpen] = useState(false)

  return (
    <Card className="flex flex-col space-y-4 p-5 mt-5 rounded-xl">
      <BountyRoundHeader round={round} difficultyLabels={component} />
      <BountyRoundStatus
        round={round}
        component={component}
        now={now}
        onKill={() => setKillDialogOpen(true)}
        onShowQr={() => setQrOpen(true)}
      />
      {!round.finalized && round.myTeam && round.myTeam.kills > 0 && <p>Gyilkosságok: {round.myTeam.kills} db</p>}
      <BountyRoundDialogs
        roundName={round.name}
        registration={round.myRegistration}
        killDialogOpen={killDialogOpen}
        onKillDialogOpenChange={setKillDialogOpen}
        qrOpen={qrOpen}
        onQrOpenChange={setQrOpen}
      />
    </Card>
  )
}

const BountyRoundHeader = ({ round, difficultyLabels }: { round: BountyRoundView; difficultyLabels: Bounty }) => {
  const difficultyLabel: Record<string, string | undefined> = {
    EASY: difficultyLabels?.easyRoundLabel,
    MEDIUM: difficultyLabels?.mediumRoundLabel,
    HARD: difficultyLabels?.hardRoundLabel
  }

  return (
    <div className="flex flex-row justify-between items-center flex-wrap gap-2">
      <h2 className="m-0 text-2xl font-bold">{round.name}</h2>
      <div className="text-sm opacity-70 flex items-center gap-1 [&_p]:mb-0">
        Nehézség: <Markdown text={difficultyLabel[round.difficulty]} />
      </div>
    </div>
  )
}

const BountyRoundStatus = ({
  round,
  component,
  now,
  onKill,
  onShowQr
}: {
  round: BountyRoundView
  component: Bounty
  now: number
  onKill: () => void
  onShowQr: () => void
}) => {
  if (round.finalized) return <FinalizedRoundResults round={round} component={component} />
  if (!round.myRegistration) return <RegistrationRoundDetails round={round} component={component} />

  const registration = round.myRegistration
  if (!registration.alive) return <EliminatedPlayerStatus registration={registration} team={round.myTeam} component={component} />
  if (!round.myTeam) return <WaitingForTeamStatus round={round} now={now} />
  if (round.phase === 'ACTIVE') {
    return (
      <ActiveRoundContent round={round} registration={registration} team={round.myTeam} now={now} onKill={onKill} onShowQr={onShowQr} />
    )
  }

  return <PreGameStatus round={round} />
}

const RegistrationRoundDetails = ({ round, component }: { round: BountyRoundView; component: Bounty }) => {
  const fullRegistrationMessage = round.phase === 'REGISTRATION' && round.registrationFull ? component?.registrationFullMessage : ''
  const registrationMessage = fullRegistrationMessage
    ? fullRegistrationMessage
    : round.phase === 'BEFORE_REGISTRATION'
      ? component?.registrationUpcomingMessage
      : round.phase === 'REGISTRATION'
        ? component?.registrationInfo
        : component?.registrationClosedMessage

  return (
    <div>
      <Markdown text={registrationMessage} />
      <p className="mt-2">
        Regisztráció: {stringifyTimeStamp(round.registrationStart, TIMESTAMP_OPTIONS)} -{' '}
        {stringifyTimeStamp(round.registrationEnd, TIMESTAMP_OPTIONS)}
      </p>
      <p>
        Játék: {stringifyTimeStamp(round.gameStart, TIMESTAMP_OPTIONS)} - {stringifyTimeStamp(round.gameEnd, TIMESTAMP_OPTIONS)}
      </p>
    </div>
  )
}

const EliminatedPlayerStatus = ({
  registration,
  team,
  component
}: {
  registration: BountyRegistrationView
  team: BountyTeamView | null
  component: Bounty
}) => (
  <div className="rounded-lg border border-danger bg-danger p-3 text-danger-foreground">
    <div className="font-bold flex items-center gap-2 [&_p]:mb-0">
      <Skull className="h-4 w-4" />
      <Markdown text={registration.eliminatedByInactivity ? component?.eliminatedByInactivityMessage : component?.killedMessage} />
    </div>
    {team && team.aliveCount === 0 && (
      <div className="mt-1 [&_p]:mb-0">
        <Markdown text={component?.teamEliminatedMessage} />
      </div>
    )}
  </div>
)

const FinalizedRoundResults = ({ round, component }: { round: BountyRoundView; component: Bounty }) => {
  const team = round.myTeam

  return (
    <div>
      {team &&
        (team.winner ? (
          <div className="text-lg font-bold text-success [&_p]:mb-0">
            <Markdown text={component?.winnerMessage} />
          </div>
        ) : (
          <p>
            Helyezés: <b>{team.rank}.</b>
          </p>
        ))}
      {team && (
        <p>
          Gyilkosságok: <b>{team.kills} db</b>
        </p>
      )}
      {round.winnerGroupName && <p>Győztes: {round.winnerGroupName}</p>}
    </div>
  )
}

const WaitingForTeamStatus = ({ round, now }: { round: BountyRoundView; now: number }) =>
  now < round.gameStart ? (
    <p>A kör {stringifyTimeStamp(round.gameStart, TIMESTAMP_OPTIONS)}-kor kezdődik.</p>
  ) : (
    <p>A sorsolás hamarosan megtörténik.</p>
  )

const PreGameStatus = ({ round }: { round: BountyRoundView }) => (
  <p>A játék {stringifyTimeStamp(round.gameStart, TIMESTAMP_OPTIONS)}-kor kezdődik.</p>
)

const ActiveRoundContent = ({
  round,
  registration,
  team,
  now,
  onKill,
  onShowQr
}: {
  round: BountyRoundView
  registration: BountyRegistrationView
  team: BountyTeamView
  now: number
  onKill: () => void
  onShowQr: () => void
}) => {
  const killDeadline = registration.deadline !== null ? Math.min(registration.deadline, round.gameEnd) : null
  const isGameEndCloserThanDeadline = (registration.deadline || 0) >= round.gameEnd
  const isDeadlineReached = killDeadline !== null ? killDeadline <= now : null

  return (
    <div className="flex flex-col space-y-4">
      <div className="rounded-lg border border-primary/30 bg-primary/5 p-3">
        <p className="font-bold flex items-center gap-2">
          <Crosshair className="h-4 w-4" /> <span>A célpontod: {team.targetGroupName ?? '?'}</span>
        </p>
        <p className="flex items-center gap-2">
          <Sword className="h-4 w-4" />
          <span>Fegyver: {team.weapon}</span>
        </p>
        <p className="mt-2">
          Élő játékosok a csapatodban: <b>{team.aliveCount}</b>
        </p>
        <p>
          Élő játékosok a célpont csapatában: <b>{team.targetAliveCount ?? 0}</b>
        </p>
      </div>

      {killDeadline !== null && (
        <p>
          {isDeadlineReached ? (
            <span>Lejárt az idő!</span>
          ) : isGameEndCloserThanDeadline ? (
            <span>
              Ennyi idő maradt a körből: <b>{formatRemaining(killDeadline - now)}</b>
            </span>
          ) : (
            <span>
              Ölnöd kell ennyi időn belül: <b>{formatRemaining(killDeadline - now)}</b>
            </span>
          )}
        </p>
      )}

      {isDeadlineReached ? (
        <p>A pontok hamarosan kiszámításra kerülnek.</p>
      ) : (
        <div className="flex flex-row gap-2">
          {registration.code && (
            <Button onClick={onShowQr}>
              <Flag className="h-4 w-4" /> Megtaláltak
            </Button>
          )}
          <Button variant="destructive" onClick={onKill}>
            <Crosshair className="h-4 w-4" /> Gyilkolás
          </Button>
        </div>
      )}
    </div>
  )
}

const BountyRoundDialogs = ({
  roundName,
  registration,
  killDialogOpen,
  onKillDialogOpenChange,
  qrOpen,
  onQrOpenChange
}: {
  roundName: string
  registration: BountyRegistrationView | null
  killDialogOpen: boolean
  onKillDialogOpenChange: (open: boolean) => void
  qrOpen: boolean
  onQrOpenChange: (open: boolean) => void
}) => (
  <>
    {killDialogOpen && <KillDialog onClose={() => onKillDialogOpenChange(false)} />}
    {registration && (
      <Dialog open={qrOpen} onOpenChange={onQrOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{roundName}</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col items-center py-4">
            {registration.code && (
              <div className="w-fit max-w-full rounded-[3px] p-2" style={{ backgroundColor: '#ffffff' }}>
                <QRCode value={registration.code} />
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>
    )}
  </>
)
