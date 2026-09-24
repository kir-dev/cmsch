import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import kirDevLogoDark from '@/assets/img/kirdev-white.svg'
import kirDevLogoLight from '@/assets/img/kirdev.svg'
import { useDate } from '@/hooks/useDate.ts'
import { HIDE_KIR_DEV_IN_FOOTER } from '@/util/configs/environment.config'
import { useColorModeValue } from '@/util/core-functions.util'
import { Heart } from 'lucide-react'
import { useMemo } from 'react'
import Markdown from '../Markdown'
import { OrganizerLogo } from './OrganizerLogo'
import { PartnerLogo, type PartnerLogoName } from './PartnerLogo'
import parseSponsors from './utils/parseSponsors'

export const Footer = () => {
  const now = useDate()
  const config = useConfigContext()
  const component = config?.components?.footer
  const mainSupporters = useMemo(
    () => parseSponsors(component?.mainSupporterLogoUrls, component?.mainSupporterAlts, component?.mainSupporterWebsiteUrls),
    [component?.mainSupporterAlts, component?.mainSupporterLogoUrls, component?.mainSupporterWebsiteUrls]
  )
  const sponsors = useMemo(
    () => parseSponsors(component?.sponsorLogoUrls, component?.sponsorAlts, component?.sponsorWebsiteUrls),
    [component?.sponsorAlts, component?.sponsorLogoUrls, component?.sponsorWebsiteUrls]
  )
  const partners = useMemo(
    () => parseSponsors(component?.partnerLogoUrls, component?.partnerAlts, component?.partnerWebsiteUrls),
    [component?.partnerAlts, component?.partnerLogoUrls, component?.partnerWebsiteUrls]
  )

  const backdropFilter = useColorModeValue(config?.components?.style?.lightFooterFilter, config?.components?.style?.darkFooterFilter)
  const background = useColorModeValue(config?.components?.style?.lightFooterBackground, config?.components?.style?.darkFooterBackground)
  const bgShadowColor = useColorModeValue(
    config?.components?.style?.lightFooterShadowColor,
    config?.components?.style?.darkFooterShadowColor
  )
  const kirDevLogo = useColorModeValue(kirDevLogoLight, kirDevLogoDark)
  if (!component) return null

  const supporterGroups = [
    {
      title: component.mainSupporterTitle,
      builtInLogos: getSelectedPartnerLogos(
        component.mainBmeEnabled,
        component.mainVikEnabled,
        component.mainSchonherzEnabled,
        component.mainSchdesignEnabled
      ),
      linkedLogos: mainSupporters
    },
    {
      title: component.sponsorTitle,
      builtInLogos: getSelectedPartnerLogos(
        component.featuredBmeEnabled,
        component.featuredVikEnabled,
        component.featuredSchonherzEnabled,
        component.featuredSchdesignEnabled
      ),
      linkedLogos: sponsors
    },
    {
      title: component.partnerTitle,
      builtInLogos: getSelectedPartnerLogos(
        component.bmeEnabled,
        component.vikEnabled,
        component.schonherzEnabled,
        component.schdesignEnabled
      ),
      linkedLogos: partners
    }
  ].filter((group) => group.builtInLogos.length > 0 || group.linkedLogos.length > 0)
  const topBarVisible = supporterGroups.length > 0 && !component.minimalisticFooter

  return (
    <footer className="flex flex-col items-center w-full" style={{ backdropFilter, backgroundColor: background }}>
      {topBarVisible && (
        <div className="flex justify-center w-full p-5" style={{ backgroundColor: bgShadowColor }}>
          <div className="flex w-full max-w-full flex-col justify-center gap-4 md:max-w-5xl md:flex-row">
            {supporterGroups.map((group, index) => (
              <SupporterColumn key={index} {...group} />
            ))}
          </div>
        </div>
      )}
      <div className="flex flex-col md:flex-row px-10 py-5 gap-5 items-center w-full max-w-full md:max-w-5xl justify-between">
        {component?.footerMessage && <Markdown text={component?.footerMessage} />}

        <div className="flex flex-col md:flex-row w-full justify-center gap-5 items-center">
          <OrganizerLogo
            imageSrc={component?.hostLogo}
            websiteUrl={component?.hostWebsiteUrl}
            facebookUrl={component?.facebookUrl}
            instagramUrl={component?.instagramUrl}
            minimalistic={component?.minimalisticFooter}
          />
          {!HIDE_KIR_DEV_IN_FOOTER && (
            <OrganizerLogo
              imageSrc={kirDevLogo}
              websiteUrl={component.devWebsiteUrl}
              contactUrl={component.bugReportUrl}
              minimalistic={component.minimalisticFooter}
            />
          )}
        </div>
      </div>
      <div className="w-full text-center p-3 text-sm md:text-base" style={{ backgroundColor: bgShadowColor }}>
        Made with <Heart className="inline h-4 w-4 text-red-500 fill-red-500" /> by Kir-Dev <br /> Minden jog fenntartva. &copy;{' '}
        {now.getFullYear()}
      </div>
    </footer>
  )
}

function getSelectedPartnerLogos(bme: boolean, vik: boolean, schonherz: boolean, schdesign: boolean): PartnerLogoName[] {
  const logos: PartnerLogoName[] = []
  if (bme) logos.push('bme')
  if (vik) logos.push('vik')
  if (schonherz) logos.push('schonherz')
  if (schdesign) logos.push('schdesign')
  return logos
}

function SupporterColumn({
  title,
  builtInLogos,
  linkedLogos
}: {
  title: string
  builtInLogos: PartnerLogoName[]
  linkedLogos: ReturnType<typeof parseSponsors>
}) {
  return (
    <div className="w-full min-w-0 md:flex-1">
      {title && <h2 className="mb-3 mt-0 text-center text-xl font-bold">{title}</h2>}
      <div className="flex flex-wrap items-center justify-center">
        {builtInLogos.map((name) => (
          <PartnerLogo key={name} name={name} />
        ))}
        {linkedLogos.map((logo, index) => (
          <SponsorImage key={`${logo.image}-${index}`} {...logo} />
        ))}
      </div>
    </div>
  )
}

function SponsorImage({ image, alt, url }: { image: string; alt: string; url: string }) {
  const img = <img className="m-5 max-h-24 max-w-52 object-contain" src={image} alt={alt} loading="lazy" />
  if (url) {
    return (
      <a href={url} target="_blank" rel="noreferrer" referrerPolicy="origin">
        {img}
      </a>
    )
  }

  return img
}
