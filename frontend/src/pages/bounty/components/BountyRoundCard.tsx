import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import Markdown from '@/common-components/Markdown'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card.tsx'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { useTimeInSeconds } from '@/hooks/useDate'
import { stringifyTimeStamp, TIMESTAMP_OPTIONS } from '@/util/core-functions.util'
import type { BountyRoundView } from '@/util/views/bounty.view'
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

export const BountyRoundCard = ({ round }: { round: BountyRoundView }) => {
  const component = useConfigContext()?.components?.bounty
  const now = useTimeInSeconds(1000)
  const [killDialogOpen, setKillDialogOpen] = useState(false)
  const [qrOpen, setQrOpen] = useState(false)

  const myRegistration = round.myRegistration
  const myTeam = round.myTeam
  const isAlive = myRegistration?.alive ?? false
  const gameActive = round.phase === 'ACTIVE'
  const killDeadline = myRegistration?.deadline != null ? Math.min(myRegistration.deadline, round.gameEnd) : null
  const isGameEndCloserThanDeadline = (myRegistration?.deadline || 0) >= round.gameEnd

  const DIFFICULTY_LABEL: Record<string, string | undefined> = {
    EASY: component?.easyRoundLabel,
    MEDIUM: component?.mediumRoundLabel,
    HARD: component?.hardRoundLabel
  }

  return (
    <Card className="flex flex-col space-y-4 p-5 mt-5 rounded-xl">
      <div className="flex flex-row justify-between items-center flex-wrap gap-2">
        <h2 className="m-0 text-2xl font-bold">{round.name}</h2>
        <div className="text-sm opacity-70 flex items-center gap-1 [&_p]:mb-0">
          Nehézség: <Markdown text={DIFFICULTY_LABEL[round.difficulty]} />
        </div>
      </div>
      {!round.finalized && !myRegistration && (
        <div>
          <Markdown text={component?.registrationInfo} />
          <p className="mt-2">
            Regisztráció: {stringifyTimeStamp(round.registrationStart, TIMESTAMP_OPTIONS)} -{' '}
            {stringifyTimeStamp(round.registrationEnd, TIMESTAMP_OPTIONS)}
          </p>
          <p>
            Játék: {stringifyTimeStamp(round.gameStart, TIMESTAMP_OPTIONS)} - {stringifyTimeStamp(round.gameEnd, TIMESTAMP_OPTIONS)}
          </p>
        </div>
      )}
      {!round.finalized && myRegistration && !isAlive && (
        <div className="rounded-lg border border-danger bg-danger p-3 text-danger-foreground">
          <div className="font-bold flex items-center gap-2 [&_p]:mb-0">
            <Skull className="h-4 w-4" />
            <Markdown text={myRegistration.eliminatedByInactivity ? component?.eliminatedByInactivityMessage : component?.killedMessage} />
          </div>
          {myTeam && myTeam.aliveCount === 0 && (
            <div className="mt-1 [&_p]:mb-0">
              <Markdown text={component?.teamEliminatedMessage} />
            </div>
          )}
        </div>
      )}
      {round.finalized && (
        <div>
          {myTeam?.winner ? (
            <div className="text-lg font-bold text-success [&_p]:mb-0">
              <Markdown text={component?.winnerMessage} />
            </div>
          ) : (
            myTeam && (
              <p>
                Helyezés: <b>{myTeam.rank}.</b> - Gyilkosságok: <b>{myTeam.killPoints} pont</b>, Túlélés:{' '}
                <b>{myTeam.survivalPoints ?? 0} pont</b>
              </p>
            )
          )}
          {round.winnerGroupName && <p>Győztes: {round.winnerGroupName}</p>}
        </div>
      )}
      {!round.finalized &&
        myRegistration &&
        isAlive &&
        !myTeam &&
        (now < round.gameStart ? (
          <p>A kör {stringifyTimeStamp(round.gameStart, TIMESTAMP_OPTIONS)}-kor kezdődik.</p>
        ) : (
          <p>A sorsolás hamarosan megtörténik.</p>
        ))}
      {!round.finalized && myRegistration && isAlive && myTeam && gameActive && (
        <div className="flex flex-col space-y-4">
          <div className="rounded-lg border border-primary/30 bg-primary/5 p-3">
            <p className="font-bold flex items-center gap-2">
              <Crosshair className="h-4 w-4" /> <span>A célpontod: {myTeam.targetGroupName ?? '?'}</span>
            </p>
            <p className="flex items-center gap-2">
              <Sword className="h-4 w-4" />
              <span>Fegyver: {myTeam.weapon}</span>
            </p>
            <p className="mt-2">
              Élő játékosok a csapatodban: <b>{myTeam.aliveCount}</b>
            </p>
            <p>
              Élő játékosok a célpont csapatában: <b>{myTeam.targetAliveCount ?? 0}</b>
            </p>
          </div>

          {killDeadline !== null && (
            <p>
              {isGameEndCloserThanDeadline ? <span>Ennyi idő maradt a körből: </span> : <span>Ölnöd kell ennyi időn belül: </span>}
              <b>{formatRemaining(killDeadline - now)}</b>
            </p>
          )}

          <div className="flex flex-row gap-2">
            {myRegistration.code && (
              <Button onClick={() => setQrOpen(true)}>
                <Flag className="h-4 w-4" /> Megtaláltak
              </Button>
            )}

            <Button variant="destructive" onClick={() => setKillDialogOpen(true)}>
              <Crosshair className="h-4 w-4" /> Gyilkolás
            </Button>
          </div>
        </div>
      )}
      {!round.finalized && myRegistration && isAlive && myTeam && !gameActive && (
        <p>A játék {stringifyTimeStamp(round.gameStart, TIMESTAMP_OPTIONS)}-kor kezdődik.</p>
      )}
      {myTeam && myTeam.killPoints > 0 && <p>Gyilkosságokból szerzett pontok: {myTeam.killPoints}</p>}
      {killDialogOpen && <KillDialog onClose={() => setKillDialogOpen(false)} />}

      {!!myRegistration && (
        <Dialog open={qrOpen} onOpenChange={setQrOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{round.name}</DialogTitle>
            </DialogHeader>
            <div className="flex flex-col items-center py-4">
              {myRegistration.code && (
                <div className="w-fit max-w-full rounded-[3px] p-2" style={{ backgroundColor: '#ffffff' }}>
                  <QRCode value={myRegistration.code} />
                </div>
              )}
            </div>
          </DialogContent>
        </Dialog>
      )}
    </Card>
  )
}
