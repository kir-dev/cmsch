import GetSomeHelp from '@/assets/stop-it-get-some-help-just-stop.gif'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'

type Props = {
  isOpen: boolean
  onClose: () => void
}

export const StopItModal = ({ isOpen, onClose }: Props) => {
  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Egy kicsit lassabban!</DialogTitle>
        </DialogHeader>
        <div className="py-4">
          <img alt="Stop spamming, get some help!" src={GetSomeHelp} className="w-full rounded-md" />
        </div>
        <DialogFooter className="flex gap-2">
          <Button variant="secondary" onClick={onClose}>
            Oké
          </Button>
          <Button onClick={onClose}>Értettem</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
