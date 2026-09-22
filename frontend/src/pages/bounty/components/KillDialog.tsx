import { useBountyKillMutation } from '@/api/hooks/bounty/useBountyKillMutation'
import { QrReader } from '@/common-components/QrReader'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import type { BountyKillResponse } from '@/util/views/bounty.view'
import { Loader2, QrCode } from 'lucide-react'
import { useState } from 'react'

export const KillDialog = ({ onClose }: { onClose: () => void }) => {
  const [result, setResult] = useState<BountyKillResponse | null>(null)

  const mutation = useBountyKillMutation((response) => setResult(response))
  const hasOutcome = result !== null || mutation.isError

  const handleScan = (qrData: string) => {
    mutation.mutate(qrData)
  }

  const resetScan = () => {
    setResult(null)
    mutation.reset()
  }

  return (
    <Dialog open onOpenChange={(open) => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Gyilkolás</DialogTitle>
        </DialogHeader>

        {mutation.isPending && <Loader2 className="h-12 w-12 m-auto animate-spin p-5 text-primary" />}

        {!mutation.isPending && !hasOutcome && <QrReader onScan={handleScan} />}

        {result && (
          <Alert variant={result.success ? 'default' : 'destructive'}>
            <AlertTitle>{result.success ? 'Sikeres gyilkosság!' : 'Sikertelen gyilkosság'}</AlertTitle>
            <AlertDescription className="whitespace-pre-line">{result.message}</AlertDescription>
          </Alert>
        )}

        {!result && mutation.isError && (
          <Alert variant="destructive">
            <AlertTitle>Sikertelen gyilkosság</AlertTitle>
            <AlertDescription>Nem sikerült végrehajtani a műveletet. Próbáld újra!</AlertDescription>
          </Alert>
        )}

        {hasOutcome && (
          <Button variant="outline" className="flex items-center gap-2" onClick={resetScan}>
            <QrCode className="h-4 w-4" /> Új kód beolvasása
          </Button>
        )}
      </DialogContent>
    </Dialog>
  )
}
