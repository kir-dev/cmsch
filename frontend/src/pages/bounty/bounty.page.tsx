import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useBountyQuery } from '@/api/hooks/bounty/useBountyQuery'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'
import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { PageStatus } from '@/common-components/PageStatus'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { QrCode } from 'lucide-react'
import { useState } from 'react'
import QRCode from 'react-qr-code'
import { BountyRoundCard } from './components/BountyRoundCard'

export default function BountyPage() {
  const component = useConfigContext()?.components?.bounty
  const { data, isLoading, isError } = useBountyQuery()
  const [qrOpen, setQrOpen] = useState(false)

  if (!component) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  const noGroup = data.myGroupId == null
  const hasUnregisteredRound = data.rounds.some((round) => !round.finalized && !round.myRegistration)

  return (
    <CmschPage loginRequired title={component.title}>
      <div className="flex justify-between items-baseline flex-wrap gap-2 mb-2">
        <h1 className="text-3xl font-bold font-heading mt-5">{component.title}</h1>
        {!noGroup && data.myRegistrationQr && hasUnregisteredRound && (
          <div>
            <Button className="mt-2" onClick={() => setQrOpen(true)}>
              <QrCode className="mr-2" /> Jelentkezés
            </Button>
          </div>
        )}
      </div>
      <Markdown text={component.topMessage} />

      {noGroup && (
        <div className="mt-5 rounded-lg border border-danger bg-danger text-danger-foreground p-3">
          <p className="font-bold">A fejvadászat csapat alapú játék, ezért csapat nélkül nem tudsz regisztrálni.</p>
        </div>
      )}

      <Dialog open={qrOpen} onOpenChange={setQrOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{component.title}</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col items-center py-4">
            {data.myRegistrationQr && (
              <div className="mb-5 w-fit max-w-full rounded-[3px] p-2" style={{ backgroundColor: '#ffffff' }}>
                <QRCode value={data.myRegistrationQr} />
              </div>
            )}
          </div>
        </DialogContent>
      </Dialog>

      {data.rounds.map((round) => (
        <BountyRoundCard round={round} key={round.id} />
      ))}
    </CmschPage>
  )
}
