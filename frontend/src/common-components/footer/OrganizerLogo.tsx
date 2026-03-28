import { Separator } from '@/components/ui/separator'
import { Facebook, Instagram } from 'lucide-react'

interface OrganizerLogoProps {
  imageSrc: string
  websiteUrl?: string
  facebookUrl?: string
  instagramUrl?: string
  contactUrl?: string
  minimalistic?: boolean
}

export function OrganizerLogo({ imageSrc, minimalistic, websiteUrl, contactUrl, instagramUrl, facebookUrl }: OrganizerLogoProps) {
  const hasSocialUrls = facebookUrl || instagramUrl
  const hasUrls = websiteUrl || contactUrl || facebookUrl || instagramUrl || hasSocialUrls
  return (
    <div className="flex flex-row items-center space-x-4">
      {imageSrc && <img src={imageSrc} className="w-32 h-32 object-center object-contain" alt="Organizer logo" />}
      {!minimalistic && hasUrls && (
        <>
          <Separator orientation="vertical" className="h-20 w-px bg-border" />
          <div className="flex flex-col items-start space-y-1">
            {websiteUrl && (
              <a
                target="_blank"
                rel="noreferrer"
                className="text-xl hover:underline transition-colors hover:text-primary"
                href={websiteUrl}
              >
                Weboldal
              </a>
            )}
            {contactUrl && (
              <a
                target="_blank"
                rel="noreferrer"
                className="text-xl hover:underline transition-colors hover:text-primary"
                href={contactUrl}
              >
                Kapcsolat
              </a>
            )}
            {hasSocialUrls && (
              <div className="flex flex-row space-x-2">
                {facebookUrl && (
                  <a href={facebookUrl} target="_blank" rel="noreferrer">
                    <Facebook className="h-6 w-6" />
                  </a>
                )}
                {instagramUrl && (
                  <a href={instagramUrl} target="_blank" rel="noreferrer">
                    <Instagram className="h-6 w-6" />
                  </a>
                )}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  )
}
