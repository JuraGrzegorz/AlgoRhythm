using System;
using Microsoft.EntityFrameworkCore.Metadata.Internal;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.VisualStudio.Web.CodeGenerators.Mvc.Templates.BlazorIdentity.Pages.Manage;
using TheWebApiServer.Services.Models;

namespace TheWebApiServer.Services
{
    public class VerificationCodeService
    {
        private readonly IMemoryCache _memoryCache;
        private readonly MemoryCacheEntryOptions _cacheEntryOptions;
        Random random;

        public VerificationCodeService(IMemoryCache memoryCache)
        {
            _memoryCache = memoryCache;
            _cacheEntryOptions = new MemoryCacheEntryOptions()
                .SetAbsoluteExpiration(TimeSpan.FromHours(24));
            random=new Random();
        }

        public long GenerateCode(string email)
        {
            if (_memoryCache.TryGetValue(email, out UserVerficationData cachedObject))
            {
                /*if(cachedObject.isBloked)
                {
                    return -1;
                }
                if(cachedObject.attempts)
                cachedObject

                _memoryCache.Set(id, cachedObject);*/
                return 0;
            }
            else
            {
                UserVerficationData verificationData=new UserVerficationData();
                verificationData.attempts = 1;
                verificationData.isBloked = false;
                verificationData.Code= random.Next(10000, 100000);
                _memoryCache.Set(email, verificationData);
                return verificationData.Code;
            }
        }
        public bool VerficateCode(string email,long code)
        {
            if (_memoryCache.TryGetValue(email, out UserVerficationData cachedObject))
            {
                if (cachedObject.Code == code)
                {
                    return true;
                }
            }
            return false;
        }
    }
}
