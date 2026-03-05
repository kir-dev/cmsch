import type { Profile } from '@/api/contexts/config/types'
import { WalletButton } from '@/common-components/WalletButton'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import type { ProfileView } from '@/util/views/profile.view'
import { useState } from 'react'
import { FaQrcode } from 'react-icons/fa'
import QRCode from 'react-qr-code'

export const ProfileQR = ({ profile, component }: { profile: ProfileView; component: Profile }) => {
  const [isOpen, setIsOpen] = useState(false)

  return (
    <>
      <div className="flex flex-col items-center">
        <span className="text-3xl font-medium">{component.qrTitle}</span>
        <Button className="mt-5" onClick={() => setIsOpen(true)}>
          <FaQrcode className="mr-2" />
          QR kód felmutatása
        </Button>
        <span className="my-5 text-sm text-primary">vagy</span>
        {profile.fullName && profile.cmschId && <WalletButton type="apple" name={profile.fullName} userId={profile.cmschId} />}
        {profile.fullName && profile.cmschId && <WalletButton type="google" name={profile.fullName} userId={profile.cmschId} />}
      </div>

      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{profile.fullName}</DialogTitle>
          </DialogHeader>
          <div className="flex flex-col items-center py-4">
            {profile.cmschId ? (
              <div className="mb-5 w-fit max-w-full rounded-[3px] p-2" style={{ backgroundColor: '#ffffff' }}>
                <QRCode value={profile.cmschId} />
              </div>
            ) : (
              <p>Hiba: Cmsch azonosító nem található</p>
            )}
          </div>
        </DialogContent>
      </Dialog>
    </>
  )
}
