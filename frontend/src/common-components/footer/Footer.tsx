import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { HIDE_KIR_DEV_IN_FOOTER } from '@/util/configs/environment.config'
import { useColorModeValue } from '@/util/core-functions.util'
import { Heart } from 'lucide-react'
import { useMemo } from 'react'
import Markdown from '../Markdown'
import { OrganizerLogo } from './OrganizerLogo'
import { PartnerLogo } from './PartnerLogo'
import parseSponsors from './utils/parseSponsors'

export const Footer = () => {
  const config = useConfigContext()
  const component = config?.components?.footer
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
  const kirDevLogo = useColorModeValue('/img/kirdev.svg', '/img/kirdev-white.svg')
  if (!component) return null

  const partnersVisible = component?.bmeEnabled || component?.vikEnabled || component?.schonherzEnabled || component?.schdesignEnabled
  const topBarVisible = (component?.sponsorsEnabled || partnersVisible) && !component.minimalisticFooter
  return (
    <footer className="flex flex-col items-center w-full" style={{ backdropFilter, backgroundColor: background }}>
      {topBarVisible && (
        <div className="flex justify-center w-full p-5" style={{ backgroundColor: bgShadowColor }}>
          <div className="flex flex-col md:flex-row w-full max-w-full md:max-w-[64rem] justify-start md:justify-evenly">
            {component?.sponsorsEnabled && sponsors.length > 0 && (
              <div className="w-full">
                <h3 className="text-center font-bold text-xl mb-3 mt-0">{component.sponsorTitle}</h3>
                <div className="flex justify-center items-center flex-wrap">
                  {sponsors.map((sponsor) => (
                    <SponsorImage key={sponsor?.url} {...sponsor} />
                  ))}
                </div>
              </div>
            )}
            {partnersVisible && (
              <div className="w-full">
                <h3 className="text-center font-bold text-xl mb-3 mt-10 md:mt-0">{component.partnerTitle}</h3>
                <div className="flex justify-center items-center flex-wrap">
                  {component.bmeEnabled && <PartnerLogo name="bme" />}
                  {component.vikEnabled && <PartnerLogo name="vik" />}
                  {component.schonherzEnabled && <PartnerLogo name="schonherz" />}
                  {component.schdesignEnabled && <PartnerLogo name="schdesign" />}
                  {partners.map((partner, idx) => (
                    <SponsorImage key={idx} {...partner} />
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      )}
      <div className="flex flex-col md:flex-row px-10 py-5 gap-5 items-center w-full max-w-full md:max-w-[64rem] justify-between">
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
        {new Date().getFullYear()}
      </div>
    </footer>
  )
}

function SponsorImage({ image, alt, url }: { image: string; alt: string; url: string }) {
  const img = <img key={image} className="m-5 max-h-24 max-w-[13rem] object-contain" src={image} alt={alt} loading="lazy" />
  if (url) {
    return (
      <a href={url} key={url} target="_blank" rel="noreferrer" referrerPolicy="origin">
        {img}
      </a>
    )
  }

  return img
}
